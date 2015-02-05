package org.bardes.mplayer.citp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class CITPServer implements Runnable
{
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private MulticastSocket pinf;
	private InetSocketAddress mcastaddr;
	private ServerSocket sock;
	private ExecutorService threadPool;
	private boolean running;

	public CITPServer()
	{
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
		threadPool = Executors.newCachedThreadPool();
	}

	
	public void start()
	{
		try
		{
			mcastaddr = new InetSocketAddress("239.224.0.180", 4809);
			pinf = new MulticastSocket();
			pinf.setBroadcast(true);
			pinf.joinGroup(mcastaddr, null);
			pinf.setLoopbackMode(true);

			sock = new ServerSocket(4847);
			
			scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() { public void run() { tick(); } }, 0, 5, TimeUnit.SECONDS);
			
			threadPool.submit(this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}	

	public void tick()
	{
		sendPINF();
	}


	private void sendPINF()
	{
		int localPort = 0;
		if (sock != null && sock.isBound() && !sock.isClosed())
			localPort = sock.getLocalPort();
		
		CITPHeader header = new CITPPINFPLoc(localPort);
		sendMulti(header);
	}


	/**
	 * @param message
	 */
	private void sendMulti(CITPHeader message)
	{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		message.stream(buffer);
		
		message.fixup(buffer);

		byte[] array = buffer.array();
		DatagramPacket packet = new DatagramPacket(array, buffer.position());
		packet.setSocketAddress(mcastaddr);
		try
		{
			pinf.send(packet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * A test harness
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException
	{
	    System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
		CITPServer server = new CITPServer();
		server.start();
		
		for (;;)
		  Thread.sleep(999999999);
		
	}


	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			try
			{
				Socket accept = sock.accept();
				System.out.println("Accepted connection from: "+sock.getInetAddress());
				CITPHandler h = new CITPHandler(accept);
				threadPool.submit(h);
			}
			catch (SocketException ignore)
			{
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}


	public void stop()
	{
		running = false;
		scheduledThreadPoolExecutor.shutdown();
		threadPool.shutdown();
		try
		{
		    if (sock != null)
		        sock.close();
		}
		catch (IOException e)
		{
		}
	}
}
