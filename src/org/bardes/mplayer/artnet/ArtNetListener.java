package org.bardes.mplayer.artnet;

import static org.bardes.mplayer.sacn.N.u;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.net.NetworkListener;
import org.bardes.mplayer.personality.DMXReceiver;

public class ArtNetListener implements Runnable, NetworkListener
{
    private static final int ARTNET_PROTOCOL_VERSION = 14;
    private static final int ARTNET_PORT = 6454;
    private int universe;

    private boolean running;
    private Thread thread;
    private DatagramSocket sock;
    private DMXReceiver receiver;

    @Override
    public void start()
    {
        thread = new Thread(this);
        thread.setName("ArtNet Listener");
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void stop()
    {
        running = false;
        if (sock != null)
            sock.close();
        try
        {
            thread.join(1000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isReceiving()
    {
        return true;
    }

    @Override
    public void setReceiver(DMXReceiver receiver)
    {
        this.receiver = receiver;
    }
    
    @Override
    public DMXReceiver getReceiver()
    {
        return receiver;
    }
    
    @Override
    public void run()
    {
        running = true;
        
        Config config = Main.getConfig();
        universe = config.getUniverse();
        
        byte buffer[] = new byte[1024];
        try
        {
            sock = new DatagramSocket(ARTNET_PORT);
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            
            while (running)
            {
                p.setLength(buffer.length);
                sock.receive(p);
                ByteBuffer d = ByteBuffer.wrap(buffer);
                d.order(ByteOrder.BIG_ENDIAN);
                
                int opCode = d.getShort(8);
                if (opCode != 0x50) 
                    continue;
                
                int protocolVer = d.getShort(10);
                if (protocolVer != ARTNET_PROTOCOL_VERSION)
                    continue;
                
                int uni = u(d.get(14));
                if (uni != universe)
                    continue;
                
//                int len = d.getShort(16);
                
//                int z = 17 + offset;
                d.position(18);
                if (receiver != null)
                {
                    receiver.onFrame(d);
                }
            }
        }
        catch (SocketException ignore)
        {
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
