package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;
import static org.bardes.mplayer.sacn.N.us;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bardes.mplayer.Layer;

@SuppressWarnings("restriction")
public class GreenHippoV3_0_x implements Personality
{
    private int dimmer;
    private int mixermode;
    private int xShift;
    private int yShift;
    private int xScale;
    private int yScale;
    private int rotate;
    private int red;
    private int green;
    private int blue;
    private int brightness;
    private int contrast;
    private int negative;
    private int bank;
    private int slot;
    private int volume;
    private int pan;

    @Override
    public int getFootprint()
    {
        return 50;
    }

    @Override
    public void decode(ByteBuffer d)
    {
        int offset = d.position();
        d.order(ByteOrder.BIG_ENDIAN);
        
        dimmer = u(d.get());
        mixermode = u(d.get());
        red = u(d.get());
        green = u(d.get());
        blue = u(d.get());
        brightness = u(d.get());
        contrast = u(d.get());
        negative = u(d.get());

        rotate = us(d.getShort()) * 2;
        
        d.position(offset + 10);
        
        xShift = us(d.getShort());
        yShift = us(d.getShort());
        int aspect = u(d.get());
        int zoom = us(d.getShort());
        
        // Skip a bunch of non-analogous features
        d.position(offset + 33);
        bank = u(d.get()); // 34
        slot = u(d.get()); // 35
        
        d.position(offset + 48);
        volume = u(d.get());
        pan = u(d.get());
        
        xScale = zoom;
        yScale = zoom;
    }

    @Override
    public void activate(Layer layer)
    {
        layer.setItem(bank, slot);
        layer.setVolume(volume, pan);
        layer.shift(xShift, yShift, xScale, yScale, rotate);
        layer.setDimmer(dimmer);
        layer.setPlayMode(2);
        layer.colorAdjust(brightness, contrast, 128, 128);
    }
}
