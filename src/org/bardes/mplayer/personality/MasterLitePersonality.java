package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.us;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.MasterLayer;

public class MasterLitePersonality implements Personality
{
    private List<Personality> layers = new ArrayList<Personality>();
    int footprint = 10;
    private MasterLayer master;
    
    public MasterLitePersonality(MasterLayer master, List<Layer> layers)
    {
        this.master = master;
        for (Layer layer : layers)
        {
            LayerLitePersonality p = new LayerLitePersonality(layer);
            this.layers.add(p);
            footprint += p.getFootprint();
        }
    }

    @Override
    public int getFootprint()
    {
        return footprint;
    }

    @Override
    public void process(ByteBuffer dmxStream)
    {
        final int xShift = us(dmxStream.getShort());
        final int yShift = us(dmxStream.getShort());
        final int xScale = us(dmxStream.getShort());
        final int yScale = us(dmxStream.getShort());
        final int rotate = us(dmxStream.getShort());
        
        for (Personality p : layers)
        {
            p.process(dmxStream);
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                master.shift(xShift, yShift, xScale, yScale, rotate);
            }
        });
    }
}
