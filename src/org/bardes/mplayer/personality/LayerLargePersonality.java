package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.u;

import java.nio.ByteBuffer;

import org.bardes.mplayer.Layer;

import javafx.application.Platform;

public class LayerLargePersonality extends LayerRegularPersonality
{

	public LayerLargePersonality(Layer layer)
	{
		super(layer);
	}
	
	@Override
	public int getFootprint()
	{
		return 23;
	}

	@Override
	public void process(ByteBuffer dmxStream)
	{
		super.process(dmxStream);
		
	    final int a1 = u(dmxStream.get());
	    final int a2 = u(dmxStream.get());
	    final int b1 = u(dmxStream.get());
	    final int b2 = u(dmxStream.get());
	    final int c1 = u(dmxStream.get());
	    final int c2 = u(dmxStream.get());
	    final int d1 = u(dmxStream.get());
	    final int d2 = u(dmxStream.get());
	    
        Platform.runLater(new Runnable(){
            @Override
            public void run()
            {
                layer.shapper(a1,a2,b1,b2,c1,c2,d1,d2);
            }
        });

	}
}
