package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.us;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

@SuppressWarnings("restriction")
public class MasterLitePersonality implements Personality
{
    int footprint = 10;
    private MasterLayer master;
	private int xShift;
	private int yShift;
	private int xScale;
	private int yScale;
	private int rotate;
    
    public MasterLitePersonality(MasterLayer master, List<Layer> layers)
    {
        this.master = master;
    }

    @Override
    public int getFootprint()
    {
        return footprint;
    }

    @Override
    public void decode(ByteBuffer dmxStream)
    {
        xShift = us(dmxStream.getShort());
        yShift = us(dmxStream.getShort());
        xScale = us(dmxStream.getShort());
        yScale = us(dmxStream.getShort());
        rotate = us(dmxStream.getShort());
    }

    @Override
    public void activate(Layer layer)
    {
    	master.shift(xShift, yShift, xScale, yScale, rotate);
    }
}
