package org.bardes.mplayer.citp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
public class MSEXVStream extends MSEXHeader
{
    private Lock lock = new ReentrantLock();
    private Condition ready = lock.newCondition();
    private WritableImage image;
    private int sourceIdentifier;

    public MSEXVStream(int sourceIdentifier)
    {
        super("StFr");
        this.sourceIdentifier = sourceIdentifier;
    }

    public static CITPHeader scan(ByteBuffer buffer)
    {
        int sourceIdentifier = buffer.getShort();
        
        return new MSEXVStream(sourceIdentifier);
    }
    
    @Override
    public void stream(ByteBuffer bc)
    {
        snap();
        
        super.stream(bc);
        
        bc.putShort((short) sourceIdentifier);
        bc.put("JPEG".getBytes(), 0, 4);
        bc.putShort((short) 128);
        bc.putShort((short) 128);
        
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "JPEG", out);
            
            bc.putShort((short) out.size());
            bc.put(out.toByteArray());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }   
    }

    void snap()
    {
        image = new WritableImage(128, 128);
        lock.lock();
        try
        {
            Platform.runLater(new Runnable() { public void run() {
                lock.lock();
                try
                {
                    Scene scene = Main.display.getScene();
                    Parent node = scene.getRoot();
                    SnapshotParameters params = new SnapshotParameters();
                    node.snapshot(params, image);
                    ready.signal();
                }
                finally
                {
                    lock.unlock();
                }
            }});
            
            ready.awaitUninterruptibly();
        }
        finally
        {
            lock.unlock();
        }
    }
}
