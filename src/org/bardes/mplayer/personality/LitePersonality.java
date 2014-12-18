package org.bardes.mplayer.personality;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.bardes.mplayer.Layer;

public class LitePersonality implements Personality
{
	private Collection<Layer> layers;

	public LitePersonality(Collection<Layer> layers)
	{
		this.layers = layers;
	}
	
	@Override
	public int getFootprint()
	{
		return 4;
	}

	@Override
	public void process(ByteBuffer d)
	{
		for (Layer layer : layers)
		{
			int dimmer = d.get();
			int groupId = d.get();
			int slotId = d.get();
			int volume = d.get();
			
			layer.setDimmer(dimmer);
			layer.setItem(groupId, slotId);
			layer.setVolume(volume);
		}
	}
}
