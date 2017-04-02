package org.bardes.mplayer.sacn;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.net.NetworkListener;
import org.bardes.mplayer.personality.DMXReceiver;

public class E131Listener implements Runnable, NetworkListener
{
	private static final int E131_PORT = 5568;
	private int universe;
	private MulticastSocket sock;
	private long lastPacket;
	private boolean running;
	private Thread thread;
    private DMXReceiver receiver;
	
	public static byte x(int i) { return (byte) ((i > 128) ? (i - 256) : i); }
	
	public E131Listener()
	{
	}
	
	@Override
	public void run()
	{
		running = true;
		
		Config config = Main.getConfig();
		universe = config.getUniverse();
		
		RootLayer.init();
		FramingLayer.init();
		DMPLayer.init();
		
		try
		{
			byte buffer[] = new byte[1024];
			
			NetworkInterface networkInterface = null;
			try
			{
			    String nwInterface = config.getNetworkInterface();
				if (nwInterface != null)
				{
				    networkInterface = NetworkInterface.getByName(nwInterface);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// 239.255.x.x
			byte addr[] = { x(239), x(255), x(universe >> 8), x(universe & 0x0ff) };
			InetAddress laddr = InetAddress.getByAddress(addr);
			InetSocketAddress socketAddress = new InetSocketAddress(laddr, E131_PORT);

			sock = new MulticastSocket(E131_PORT);
			sock.joinGroup(socketAddress, networkInterface);
			
			while (running)
			{
			    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				sock.receive(dp);
				
				lastPacket = System.currentTimeMillis();
				ByteBuffer bb = ByteBuffer.wrap(buffer, dp.getOffset(), dp.getLength());
				
				if (receiver != null)
				{
				    int start = RootLayer.bytes + FramingLayer.bytes;
				    int z = start + DMPLayer.DMX_START_CODE.getOffset();
				    
				    int startcode = bb.get(z);
				    if (startcode == 0)
				    {				    
    					bb.position(z);
    					receiver.onFrame(bb.slice());
				    }
				}
			}
		}
		catch (SocketException e)
		{
			// This is a normal exception resulting from closing the socket.
			running = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			running = false;
		}
		
		if (!sock.isClosed())
		    sock.close();
	}
	
	@Override
	public void start()
	{
		thread = new Thread(this);
		thread.setName("E1.31 Listener");
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
		return (System.currentTimeMillis() - lastPacket) < 10000; // within ten seconds
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
}
