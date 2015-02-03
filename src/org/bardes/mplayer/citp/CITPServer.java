package org.bardes.mplayer.citp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

public class CITPServer
{

	private Node node;
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private Lock lock = new ReentrantLock();
	private Condition ready;
	private MulticastSocket pinf;
	private InetSocketAddress mcastaddr;

	public CITPServer(Node node)
	{
		this.node = node;
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
		ready = lock.newCondition();
	}

	
	public void start()
	{
		try
		{
			mcastaddr = new InetSocketAddress("239.224.0.180", 4809);
			pinf = new MulticastSocket();
			pinf.setBroadcast(true);
			pinf.joinGroup(mcastaddr, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() { public void run() { tick(); } }, 0, 5, TimeUnit.SECONDS);
	}	

	public void tick()
	{
		sendPINF();
		
		if (node == null)
			return;
		
		final WritableImage image = new WritableImage(640, 480);
		lock.lock();
		try
		{
			Platform.runLater(new Runnable() { public void run() {
				lock.lock();
				try
				{
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


	private void sendPINF()
	{
		CITPHeader header = new CITPPINFPLoc();
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
		CITPServer server = new CITPServer(null);
		server.start();
		
		for (;;)
		  Thread.sleep(999999999);
	}
}
