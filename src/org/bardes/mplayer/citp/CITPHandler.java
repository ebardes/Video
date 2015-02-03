package org.bardes.mplayer.citp;

import java.net.Socket;
import java.nio.channels.SocketChannel;

public class CITPHandler implements Runnable
{

	private Socket sock;

	public CITPHandler(Socket sock)
	{
		this.sock = sock;
	}

	@Override
	public void run()
	{
		try
		{
			SocketChannel channel = sock.getChannel();
			channel.configureBlocking(true);
			
			for (;;)
			{
				CITPHeader n = new CITPHeader();
				n.scan(channel);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
