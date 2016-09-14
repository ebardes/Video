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
    private List<Personality> layers = new ArrayList<Personality>();
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
        for (Layer layer : layers)
        {
            Personality p = getLayerPersonality(layer);
            this.layers.add(p);
            footprint += p.getFootprint();
        }
    }

    public LayerLitePersonality getLayerPersonality(Layer layer)
    {
        return new LayerLitePersonality(layer);
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
        
        extra(dmxStream);
        for (Personality p : layers)
        {
            p.decode(dmxStream);
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
            	activate();
            }
        });
    }

    protected void extra(ByteBuffer dmxStream)
    {
    	// Nothing
    }
    
    @Override
    public void activate()
    {
    	master.shift(xShift, yShift, xScale, yScale, rotate);
    	for (Personality p : layers)
    	{
    	    p.activate();
    	}
    }
}
