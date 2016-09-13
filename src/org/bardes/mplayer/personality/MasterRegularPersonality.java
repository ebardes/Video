package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;
import java.util.List;

import javafx.application.Platform;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

@SuppressWarnings("restriction")
public class MasterRegularPersonality extends MasterLitePersonality
{
    int r,g,b;
    int m;
    private MasterLayer master;

    public MasterRegularPersonality(MasterLayer master, List<Layer> layers)
    {
        super(master, layers);
        this.master = master;
        footprint += 4;
    }

    @Override
    public void extra(ByteBuffer dmxStream)
    {
        final int mode = u(dmxStream.get());
        final int red = u(dmxStream.get());
        final int green = u(dmxStream.get());
        final int blue = u(dmxStream.get());
        
        if (r != red || g != green || b != blue)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    master.color(red, green, blue);
                }
            });
            r = red;
            g = green;
            b = blue;
        }
        
        if (m != mode)
        {
            m = mode;
        }
    }
    
    @Override
    public LayerLitePersonality getLayerPersonality(Layer layer)
    {
        return new LayerRegularPersonality(layer);
    }
}
