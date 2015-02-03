package org.bardes.mplayer.citp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/*
	CITP_Header
	{
		uint32 Cookie // Set to "CITP".
		uint8 VersionMajor // Set to 1.
		uint8 VersionMinor // Set to 0.
		union
		{
		uint16 RequestIndex // See below
		uint16 InResponseTo // See below
		}
		uint32 MessageSize // The size of the entire message, including this header.
		uint16 MessagePartCount // Number of message fragments.
		uint16 MessagePart // Index of this message fragment (0‐based).
		uint32 ContentType // Cookie identifying the type of contents (the name of the second layer).
	}
 */

public class CITPHeader implements Streamable
{
	int versionMajor = 1;
	int versionMinor = 0;
	int requestId;
	int messageSize;
	int messagePartCount;
	int messagePart;
	private String contentType;
	
	
	@Override
	public void stream(ByteBuffer bc)
	{
		bc.put((byte) 'C');
		bc.put((byte) 'I');
		bc.put((byte) 'T');
		bc.put((byte) 'P');
		
		bc.put((byte) versionMajor);
		bc.put((byte) versionMinor);
		
		bc.putShort((short) requestId); // request/response
		bc.putInt(0); // Message Size Place Holder
		bc.putShort((short) 1); // Message Part Count
		bc.putShort((short) 1); // Message Part
		bc.put(contentType.getBytes(), 0, 4);
	}

	public String getContentType()
	{
		return contentType;
	}


	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * 
	 */
	public void fixup(ByteBuffer bc)
	{
		int length = bc.position();
		bc.putInt(8, length);
		bc.position(length);
	}
	
	static void string(ByteBuffer bc, String text)
	{
		bc.put(text.getBytes());
		bc.put((byte) 0);
	}

	public void scan(InputStream in) throws IOException
	{
		byte[] type = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(20);
		in.read(buffer.array(), 0, buffer.capacity());
		buffer.position(0);
		
		buffer.getInt(); // CITP header
		versionMajor = buffer.get();
		versionMinor = buffer.get();
		buffer.position(16);
		buffer.get(type);
		contentType = new String(type);
		
		System.out.println("message: "+contentType);
		
		int packetLength = buffer.getInt(8);
		
		byte[] data = new byte[packetLength-20];
		in.read(data);
		buffer = ByteBuffer.wrap(data);
	}
}
