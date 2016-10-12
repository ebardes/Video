package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.us;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

@SuppressWarnings("restriction")
public class MasterLitePersonality implements MasterPersonality
{
    int footprint = 10;
	private int xShift;
	private int yShift;
	private int xScale;
	private int yScale;
	private int rotate;
    
    public MasterLitePersonality()
    {
    }

    @Override
    public int getFootprint()
    {
        return footprint;
    }

    @Override
    public void decode(ByteBuffer dmxStream)
    {
        dmxStream.order(ByteOrder.BIG_ENDIAN);
        
        xShift = us(dmxStream.getShort());
        yShift = us(dmxStream.getShort());
        xScale = us(dmxStream.getShort());
        yScale = us(dmxStream.getShort());
        rotate = us(dmxStream.getShort());
    }

    @Override
    public void activate(Layer layer)
    {
    	layer.shift(xShift, yShift, xScale, yScale, rotate);
    }
}
