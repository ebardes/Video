package org.bardes.mplayer;

import java.nio.ByteBuffer;

import org.bardes.mplayer.personality.Personality;

public class LayerManager implements Personality
{

	@Override
	public int getFootprint()
	{
		return 512;
	}

	@Override
	public void decode(ByteBuffer dmxStream)
	{
	}

	@Override
	public void activate(Layer layer)
	{
	}
}
