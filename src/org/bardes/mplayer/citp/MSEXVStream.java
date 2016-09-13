package org.bardes.mplayer.citp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import org.bardes.mplayer.Main;

/*
CITP_MSEX_RqSt
{
CITP_MSEX_Header CITPMSEXHeader // CITP MSEX header. MSEX ContentType is "RqSt".
uint16 SourceIdentifier // Identifier of the source requested.
uint32 FrameFormat // Requested frame format. Can be "RGB8" or "JPEG" (and "PNG ", "fJPG"
// or "fPNG" for MSEX 1.2 and up).
uint16 FrameWidth // Preferred minimum frame width.
uint16 FrameHeight // Preferred minimum frame height.
uint8 FPS // Preferred minimum frames per second.
uint8 Timeout // Timeout in seconds (for instance 5 seconds, 0 to ask for only one frame).
}
 */
@SuppressWarnings("restriction")
public class MSEXVStream extends MSEXHeader implements Runnable
{
    public static int HEIGHT = 240;
    public static int WIDTH = 360;
    static Lock lock = new ReentrantLock();
    private Condition ready = lock.newCondition();
    private WritableImage image;
    private int sourceIdentifier;
    private int fps;
    private int timeout;
    static boolean running = false;

    public MSEXVStream(int sourceIdentifier, int fps, int timeout)
    {
        super("StFr");
        this.sourceIdentifier = sourceIdentifier;
        this.fps = fps;
        this.timeout = timeout;
    }

    public static CITPHeader scan(ByteBuffer buffer)
    {
        lock.lock();
        try
        {
            if (running)
                return null;
            
            running = true;
            int sourceIdentifier = buffer.getShort();
            
            int fps = buffer.get(16);
            int timeout = buffer.get(17);
            
            MSEXVStream x = new MSEXVStream(sourceIdentifier, fps, timeout);
            CITPServer.threadPool.submit(x);
        }
        finally
        {
            lock.unlock();
        }
        return null; 
    }
    
    @Override
    public void stream(ByteBuffer bc)
    {
        snap();
        
        if (image == null)
            return;
        
        super.stream(bc);
        
        bc.putShort((short) sourceIdentifier);
        bc.put("JPEG".getBytes(), 0, 4);
        bc.putShort((short) HEIGHT);
        bc.putShort((short) WIDTH);
        
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage bi = new BufferedImage((int)image.getWidth(), (int)image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            bi = SwingFXUtils.fromFXImage(image, bi);
            
            BufferedImage bi2 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = bi2.createGraphics();
            
            g.drawImage(bi, 0, 0, WIDTH, HEIGHT, null);
            /*
            g.setColor(Color.BLACK);
            g.drawString("Astreaus Media Server", 3, 13);
            g.setColor(Color.WHITE);
            g.drawString("Astreaus Media Server", 2, 12);
            */
            g.dispose();
            
            ImageIO.write(bi2, "JPEG", out);
            bc.putShort((short) out.size());
            bc.put(out.toByteArray());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }   
    }

    private class Snaplet implements Runnable
    {
        public void run()
        {
            lock.lock();
            try
            {
                Scene scene = Main.display.getScene();
                image = scene.snapshot(null);
            }
            catch (Exception ops)
            {
            }
            finally
            {
                ready.signal();
                lock.unlock();
            }
        }
    }
    
    void snap()
    {
        image = null;
        lock.lock();
        try
        {
            Platform.runLater(new Snaplet());
            ready.awaitUninterruptibly();
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void run()
    {
        int i = 0;
        long delay = 1000 / fps;
        long stop = System.currentTimeMillis() + (1000 * timeout);
        
        while (System.currentTimeMillis() < stop)
        {
            try
            {
                Thread.currentThread().setName("Frame:" + i);
                i++;
                CITPServer.sendMulti(this);
                Thread.sleep(delay);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        lock.lock();
        try
        {
            running = false;
        }
        finally
        {
            lock.unlock();
        }
    }
}
