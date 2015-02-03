package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public class CITPPINFPLoc extends CITPPINFHeader
{
	public CITPPINFPLoc()
	{
		super("PLoc");
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		bc.putShort((short) 0);
		string(bc, "MediaServer"); // Type
		string(bc, ASTREAUS_MEDIA_SERVER); // Name
		string(bc, "Disrepair");
	}
}
