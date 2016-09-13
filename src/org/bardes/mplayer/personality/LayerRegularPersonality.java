package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;
import static org.bardes.mplayer.sacn.N.us;

import java.nio.ByteBuffer;

import org.bardes.mplayer.Layer;

public class LayerRegularPersonality extends LayerLitePersonality
{
    private int xShift;
	private int yShift;
	private int xScale;
	private int yScale;
	private int rotate;
	private int playMode;

	public LayerRegularPersonality(Layer layer)
    {
        super(layer);
    }
    
    @Override
    public void decode(ByteBuffer dmxStream)
    {
        super.decode(dmxStream);
        xShift = us(dmxStream.getShort());
        yShift = us(dmxStream.getShort());
        xScale = us(dmxStream.getShort());
        yScale = us(dmxStream.getShort());
        rotate = us(dmxStream.getShort());
        playMode = u(dmxStream.get());
    }
    
    @Override
    public void activate()
    {
    	layer.shift(xShift, yShift, xScale, yScale, rotate);
    	layer.setPlayMode(playMode);
    	super.activate();
    }

    @Override
    public int getFootprint()
    {
        return 15;
    }
}
