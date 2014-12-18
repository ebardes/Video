package org.bardes.mplayer.sacn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.net.NetworkListener;
import org.bardes.mplayer.personality.Personality;

public class E131Listener implements Runnable, NetworkListener
{
	private static final int E131_PORT = 5568;
	private int offset;
	private int universe;
	private DatagramSocket sock;
	private long lastPacket;
	private boolean running;
	private Thread thread;
	private Personality personality;
	
	private static byte x(int i) { return (byte) ((i > 128) ? (i - 256) : i); }
	
	public E131Listener()
	{
	}
	
	@Override
	public void run()
	{
		running = true;
		
		Config config = Main.getConfig();
		offset = config.getOffset();
		universe = config.getUniverse();
		
		RootLayer.init();
		FramingLayer.init();
		DMPLayer.init();
		
		try
		{
			byte buffer[] = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
			
			// 239.255.x.x
			byte addr[] = { x(239), x(255), x(universe >> 8), x(universe & 0x0ff) };
			InetAddress laddr = InetAddress.getByAddress(addr);
			sock = new DatagramSocket(E131_PORT, laddr);
			sock.setBroadcast(true);
			
			while (running)
			{
				dp.setLength(buffer.length);
				sock.receive(dp);
				
				lastPacket = System.currentTimeMillis();
				ByteBuffer bb = ByteBuffer.wrap(buffer);
				
				
				System.out.println(N.getInt(bb, RootLayer.RLP_FLAGS_AND_LENGTH, 0));
				if (personality != null)
				{
					ByteBuffer d = ByteBuffer.wrap(buffer, offset, personality.getFootprint());
					personality.process(d);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			running = false;
		}
	}
	
	@Override
	public void start()
	{
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void stop()
	{
		running = false;
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
	public void setPersonality(Personality personality)
	{
		this.personality = personality;
		
	}

	@Override
	public Personality getPersonality()
	{
		return personality;
	}	
}
