package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;

import javafx.application.Platform;

import org.bardes.mplayer.Layer;


public class LayerLitePersonality implements Personality
{
    protected Layer layer;
    
	public LayerLitePersonality(Layer layer)
    {
        this.layer = layer;
    }

    @Override
	public int getFootprint()
	{
		return 4;
	}

	@Override
	public void process(ByteBuffer d)
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
