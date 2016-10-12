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
    private int red;
    private int green;
    private int blue;

    public MasterRegularPersonality()
    {
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
    public void activate(Layer master)
    {
        if (r != red || g != green || b != blue)
        {
            r = red;
            g = green;
            b = blue;
//            master.color(r, g, b);
        }
        super.activate(master);
    }
}
