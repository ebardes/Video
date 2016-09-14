package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;

import org.bardes.mplayer.Layer;

public class LayerLargePersonality extends LayerRegularPersonality
{

	private int a1;
	private int a2;
	private int b1;
	private int b2;
	private int c1;
	private int c2;
	private int d1;
	private int d2;

	public LayerLargePersonality(Layer layer)
	{
		super(layer);
	}
	
	@Override
	public int getFootprint()
	{
		return 23;
	}

	@Override
	public void decode(ByteBuffer dmxStream)
	{
		super.decode(dmxStream);
		
	    a1 = u(dmxStream.get());
	    a2 = u(dmxStream.get());
	    b1 = u(dmxStream.get());
	    b2 = u(dmxStream.get());
	    c1 = u(dmxStream.get());
	    c2 = u(dmxStream.get());
	    d1 = u(dmxStream.get());
	    d2 = u(dmxStream.get());
	}
	
	@Override
	public void activate()
	{
		layer.shapper(a1,a2,b1,b2,c1,c2,d1,d2);
		super.activate();
	}
}
