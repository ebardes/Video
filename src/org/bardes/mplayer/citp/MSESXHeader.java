package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public abstract class MSESXHeader extends CITPHeader
{
	private String type;

	public MSESXHeader(String type)
	{
		this.type = type;
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		
		bc.put((byte) 1);
		bc.put((byte) 1);
		bc.put(type.getBytes(), 0, 4);
	}
}
