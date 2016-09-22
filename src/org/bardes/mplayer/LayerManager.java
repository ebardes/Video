package org.bardes.mplayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.bardes.mplayer.personality.DMXPersonality;
import org.bardes.mplayer.personality.DMXReceiver;
import org.bardes.mplayer.personality.MasterPersonality;
import org.bardes.mplayer.personality.Personality;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

@SuppressWarnings("restriction")
public class LayerManager implements DMXReceiver
{
    static class LayerInfo
    {
        int start;
        int footprint;
        public DMXPersonality personality;
        public Personality node;
        public Layer layer;
        public byte[] bytes;
		public void setPreviewPane(BorderPane borderPane)
		{
			layer.setPreviewPane(borderPane);
		}
    }
    
    List<LayerInfo> layers = new ArrayList<>();
    @SuppressWarnings("unused")
	private MasterLayer master;
    private LayerInfo masterInfo;
    private boolean debugging = Main.getConfig().isDebugEnabled();
    
    public LayerManager(StackPane root, List<LayerConfig> layerConfig)
    {
        final ObservableList<Node> children = root.getChildren();
		children.clear();
        master = new MasterLayer(root);
        
        int layerId = 0;
        for (LayerConfig x : layerConfig)
        {
            LayerInfo info = new LayerInfo();
            info.personality = x.getPersonality();
            info.start = x.getAddress();
            info.node = info.personality.getInstance();
            info.footprint = info.node.getFootprint();
            info.bytes = new byte[info.footprint];
            
            BorderPane p = new BorderPane();
            if (debugging)
            {
	            Text text = new Text();
	            text.setText(String.format("Layer %d - %s", layerId+1, info.personality.toString()));
	            text.setFill(Color.BISQUE);
	            p.setBottom(text);
            }
            if (info.node instanceof MasterPersonality)
            {
                masterInfo = info;
            }
            else
            {
                info.layer = new BasicLayer(layerId++, p);
                layers.add(info);
                children.add(0, p);
            }
        }
    }

    @Override
    public void onFrame(ByteBuffer frame)
    {
        boolean anyChange = false;
        int start = frame.position();
        
        for (LayerInfo x : layers)
        {
            frame.position(start + x.start);
            boolean changed = false;
            for (int i = 0; i < x.footprint; i++)
            {
                byte b = frame.get();
                if (x.bytes[i] != b)
                {
                    x.bytes[i] = b;
                    changed = true;
                }
            }
            if (changed)
            {
            	if (debugging)
            	{
	                for (int i = 0; i < x.footprint; i++)
	                {
	                    System.out.format("%02x ", x.bytes[i] & 0xff);
	                }
	                System.out.println();
            	}
                ByteBuffer bb = ByteBuffer.wrap(x.bytes);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                x.node.decode(bb);
                anyChange = true;
            }
        }
        if (masterInfo != null)
        {
            frame.position(start + masterInfo.start);
            masterInfo.node.decode(frame);
        }
        
        if (anyChange)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                	for (LayerInfo x : layers)
                	{
                		x.node.activate(x.layer);
                	}
                    if (masterInfo != null)
                    {
                        masterInfo.node.activate(null);
                    }
                }
            });
        }
    }
}
