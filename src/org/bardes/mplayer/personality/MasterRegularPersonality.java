package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;
import java.util.List;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

public class MasterRegularPersonality extends MasterLitePersonality
{
    int r,g,b;
    int m;
    private MasterLayer master;
    private int red;
    private int green;
    private int blue;

    public MasterRegularPersonality(MasterLayer master, List<Layer> layers)
    {
        super(master, layers);
        this.master = master;
        footprint += 4;
    }

    @Override
    public void decode(ByteBuffer dmxStream)
    {
        final int mode = u(dmxStream.get());
        red = u(dmxStream.get());
        green = u(dmxStream.get());
        blue = u(dmxStream.get());
        
        
        if (m != mode)
        {
            m = mode;
        }
    }
    
    @Override
    public void activate(Layer layer)
    {
        if (r != red || g != green || b != blue)
        {
            r = red;
            g = green;
            b = blue;
            master.color(r, g, b);
        }
        super.activate(layer);
    }
}
