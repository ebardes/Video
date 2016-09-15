package org.bardes.mplayer.net;

import static org.bardes.mplayer.sacn.E131Listener.x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXB;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.GroupSlot;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.Slot;

public class Replication
{
	public static final int PORT = 13268;
	
	private boolean running = true;

	private MulticastSocket sock;

	private InetSocketAddress socketAddress;

	private ServerSocket transfersocket;

	private ExecutorService threadPool;
	
	
	public Replication()
	{
		threadPool = Executors.newSingleThreadExecutor();
	}
	
	private void announcementlistener()
	{
		Timer refresh = new Timer("replication timer", true);
		try
		{
			byte addr[] = { x(239), x(250), 0, 1 };
			
			sock = new MulticastSocket(PORT);
			sock.setLoopbackMode(false);
			NetworkInterface networkInterface = sock.getNetworkInterface();
			InetAddress laddr = Inet4Address.getByAddress(addr);
			socketAddress = new InetSocketAddress(laddr, PORT);
			sock.joinGroup(socketAddress, networkInterface);
			
			refresh.scheduleAtFixedRate(new TimerTask(){ public void run() { tick(); }}, 0, 5000);
			
			byte[] buf = new byte[8192];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			while (running) 
			{
				sock.receive(p);
				process(p);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			refresh.cancel();
			if (sock != null)
				sock.close();
			sock = null;
		}
	}
	
	private void transferlistener()
	{
		try
		{
			transfersocket = new ServerSocket(PORT);
			try
			{
				while (running)
				{
					Socket accept = transfersocket.accept();
					try
					{
						InputStream inputStream = accept.getInputStream();
						IWantPacket wantPacket = JAXB.unmarshal(inputStream, IWantPacket.class);
					}
					finally
					{
						accept.close();
					}
				}
			}
			finally
			{
				transfersocket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void process(DatagramPacket p)
	{
		boolean want = false;
		
		ByteArrayInputStream in = new ByteArrayInputStream(p.getData(), 0, p.getLength());
		IHavePacket havePacket = JAXB.unmarshal(in, IHavePacket.class);
		
		int groupId = havePacket.getGroup();
		int slotId = havePacket.getSlot();
		long timestamp = havePacket.getTimestamp();
		
		Config config = Main.getConfig();
		GroupSlot group = config.getGroup(groupId);
		
		if (group == null)
		{
			want = true;
		}
		else
		{
			Slot slot = group.get(slotId);
			if (slot == null)
			{
				want = true;
			}
			else
			{
				long localTimestamp = slot.getTimestamp();
				if (localTimestamp < timestamp)
					want = true;
			}
		}
		
		if (want)
		{
			final InetAddress remote = p.getAddress();
			final IWantPacket wantPacket = new IWantPacket();
			wantPacket.setGroup(groupId);
			wantPacket.setSlot(slotId);

			threadPool.submit(new Runnable(){
				public void run()
				{
					download(remote, wantPacket);
				}});
		}
	}

	/**
	 * Connect to the remote system and retrieve the content.
	 * @param remote
	 * @param wantPacket
	 */
	protected void download(InetAddress remote, IWantPacket wantPacket)
	{
		try
		{
			Socket sock = new Socket(remote, PORT);
			try
			{
				OutputStream outputStream = sock.getOutputStream();
				InputStream inputStream = sock.getInputStream();
				
				JAXB.marshal(wantPacket, outputStream);
				outputStream.close();
				DataInputStream data = new DataInputStream(inputStream);

				String xmlBlob = data.readUTF();
				Slot slot = JAXB.unmarshal(new StringReader(xmlBlob), Slot.class);
				
				byte buffer[] = new byte[8000];
				int n;
				while ((n = data.read(buffer)) > 0)
				{
					
				}
			}
			finally
			{
				sock.close();
			}
		}
		catch (Exception e)
		{
		}
	}

	protected void tick()
	{
		byte[] buf = new byte[8192];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		
		Config config = Main.getConfig();
		config.getGroups().forEach(g -> {
			if (g.getSlot() == 0) // Skip the system group
				return;
			
			g.slots.values().forEach(s -> {
				IHavePacket x = new IHavePacket(g, s);
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXB.marshal(x, out);
				if (out.size() < buf.length) {
					try
					{
						p.setData(out.toByteArray());
						p.setSocketAddress(socketAddress);
						sock.send(p);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		});
	}

	public void start()
	{
		Thread thread = new Thread(new Runnable() { public void run() { announcementlistener(); }});
		thread.setName("replication main");
		thread.setDaemon(true);
		thread.start();
		
		thread = new Thread(new Runnable() { public void run() { transferlistener(); }});
		thread.setName("replication main");
		thread.setDaemon(true);
		thread.start();
		
	}
}
