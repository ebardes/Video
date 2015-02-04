package org.bardes.mplayer.citp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CITPHandler implements Runnable
{

	private Socket sock;
	private InputStream is;
	private OutputStream os;

	public CITPHandler(Socket sock) throws IOException
	{
		this.sock = sock;
		is = sock.getInputStream();
		os = sock.getOutputStream();
	}
	
	public void close() throws IOException
	{
		sock.close();
	}

	@Override
	public void run()
	{
		try
		{
			send(new CITPPINFPNam());
			send(new MSEXSinf());
			for (;;)
			{
			    if (sock.isClosed() || sock.isInputShutdown())
			        break;
			    
				CITPHeader n = CITPHeader.scan(is);
				if (n != null)
				    send(n);
			}
		}
        catch (EOFException ignore)
        {
        }
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		    System.out.println("Closed");
		    try
            {
                close();
            }
            catch (IOException ignore)
            {
            }
		}
	}

	private void send(CITPHeader x) throws IOException
	{
		byte[] buffer = new byte[8192];
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		x.stream(bb);
		x.fixup(bb);
		
		os.write(buffer, 0, bb.position());
	}

}
