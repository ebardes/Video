package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;
import java.util.Collection;

import javafx.application.Platform;

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
		return 4 * layers.size();
	}

	@Override
	public void process(ByteBuffer d)
	{
		for (final Layer layer : layers)
		{
			final int dimmer = u(d.get());
			final int groupId = u(d.get());
			final int slotId = u(d.get());
			final int volume = u(d.get());
			
			Platform.runLater(new Runnable(){
                @Override
                public void run()
                {
                    layer.setItem(groupId, slotId);
                    layer.setDimmer(dimmer);
                    layer.setVolume(volume);
                }
            });
		}
	}
}
