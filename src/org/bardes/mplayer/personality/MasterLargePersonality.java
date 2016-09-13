package org.bardes.mplayer.personality;

import java.util.List;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

public class MasterLargePersonality extends MasterRegularPersonality
{

	public MasterLargePersonality(MasterLayer master, List<Layer> layers)
	{
		super(master, layers);
	}

    @Override
    public LayerLitePersonality getLayerPersonality(Layer layer)
    {
        return new LayerLargePersonality(layer);
    }

}
