package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;

import org.bardes.mplayer.Layer;

public class LayerLargePersonality extends LayerRegularPersonality
{
    private int blade_1a;
    private int blade_1b;
    private int blade_2a;
    private int blade_2b;
    private int blade_3a;
    private int blade_3b;
    private int blade_4a;
    private int blade_4b;

    @Override
    public int getFootprint()
    {
        return 23;
    }

    @Override
    public void decode(ByteBuffer dmxStream)
    {
        super.decode(dmxStream);

        blade_1a = u(dmxStream.get());
        blade_1b = u(dmxStream.get());
        blade_2a = u(dmxStream.get());
        blade_2b = u(dmxStream.get());
        blade_3a = u(dmxStream.get());
        blade_3b = u(dmxStream.get());
        blade_4a = u(dmxStream.get());
        blade_4b = u(dmxStream.get());
    }

    @Override
    public void activate(Layer layer)
    {
        layer.shapper(blade_1a, blade_1b, blade_2a, blade_2b, blade_3a, blade_3b, blade_4a, blade_4b);
        super.activate(layer);
    }
}
