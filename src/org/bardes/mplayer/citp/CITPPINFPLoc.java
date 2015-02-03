package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public class CITPPINFPLoc extends CITPPINFHeader
{
	private int localPort;

	public CITPPINFPLoc(int localPort)
	{
		super("PLoc");
		this.localPort = localPort;
	}

	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		bc.putShort((short) localPort);
		string(bc, "MediaServer"); // Type
		string(bc, ASTREAUS_MEDIA_SERVER); // Name
		string(bc, "Disrepair");
	}
}
