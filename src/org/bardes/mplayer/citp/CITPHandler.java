package org.bardes.mplayer.citp;

import java.io.InputStream;
import java.net.Socket;

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
			for (;;)
			{
				CITPHeader n = new CITPHeader();
				InputStream is = sock.getInputStream();
				n.scan(is);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
