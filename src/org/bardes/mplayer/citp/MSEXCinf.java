package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public class MSEXCinf extends MSESXHeader
{
	public MSEXCinf()
	{
		super("Cinf");
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		bc.put((byte) 0);
	}
}
