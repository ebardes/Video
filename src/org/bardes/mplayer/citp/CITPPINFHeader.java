package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public abstract class CITPPINFHeader extends CITPHeader
{
	public static final String ASTREAUS_MEDIA_SERVER = "Astreaus Media Server";
	public String informationName;
	
	public CITPPINFHeader(String informationName)
	{
		this.informationName = informationName;
		setContentType("PINF");
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		bc.put(informationName.getBytes(), 0, 4);
	}
}
