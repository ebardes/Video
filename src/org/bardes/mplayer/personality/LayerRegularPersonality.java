package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.us;
import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;

import javafx.application.Platform;

import org.bardes.mplayer.Layer;

public class LayerRegularPersonality extends LayerLitePersonality
{

    public LayerRegularPersonality(Layer layer)
    {
        super(layer);
    }
    
    @Override
    public void process(ByteBuffer dmxStream)
    {
        super.process(dmxStream);
        final int xShift = us(dmxStream.getShort());
        final int yShift = us(dmxStream.getShort());
        final int xScale = us(dmxStream.getShort());
        final int yScale = us(dmxStream.getShort());
        final int rotate = us(dmxStream.getShort());
        final int playMode = u(dmxStream.get());
        
        layer.setPlayMode(playMode);
        Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                layer.shift(xShift, yShift, xScale, yScale, rotate);
            }
        });

    }

    @Override
    public int getFootprint()
    {
        return 15;
    }
}
