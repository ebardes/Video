package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public class CITPPINFPNam extends CITPPINFHeader
{
	public CITPPINFPNam()
	{
		super("PNam");
	}

	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		
		string(bc, ASTREAUS_MEDIA_SERVER);
	}
}
