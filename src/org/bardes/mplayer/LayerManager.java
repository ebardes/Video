package org.bardes.mplayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.TreeSet;

import org.bardes.mplayer.personality.DMXPersonality;
import org.bardes.mplayer.personality.DMXReceiver;
import org.bardes.mplayer.personality.MasterPersonality;
import org.bardes.mplayer.personality.Personality;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class LayerManager implements DMXReceiver
{
    private static class LayerInfo implements Comparable<LayerInfo>
    {
        int start;
        int footprint;
        public DMXPersonality personality;
        public Personality node;
        public BorderPane pane;
        public Layer layer;
        public byte[] bytes;

        @Override
        public int compareTo(LayerInfo o)
        {
            return start - o.start;
        }
    }
    
    TreeSet<LayerInfo> layers = new TreeSet<>();
    private MasterLayer master;
    private LayerInfo masterInfo;
    private boolean debugging = Main.getConfig().isDebugEnabled();
    
    public LayerManager(Stage stage, List<LayerConfig> layerConfig)
    {
        int layerId = 0;
        master = new MasterLayer(stage);
        master.clear();
        
        for (LayerConfig x : layerConfig)
        {
            LayerInfo info = new LayerInfo();
            info.start = x.getAddress();
            info.personality = x.getPersonality();
            info.node = info.personality.getInstance();
            info.footprint = info.node.getFootprint();
            info.pane = new BorderPane();
            info.bytes = new byte[info.footprint];
            if (info.node instanceof MasterPersonality)
            {
                masterInfo = info;
            }
            else
            {
                info.layer = new BasicLayer(layerId++, stage, info.pane);
                master.add(info.pane);
                layers.add(info);
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
                for (int i = 0; i < x.footprint && debugging; i++)
                {
                    System.out.format("%02x ", x.bytes[i] & 0xff);
                }
                System.out.println();
                
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
                    layers.forEach(x -> {
                        x.node.activate(x.layer);
                    });
                    if (masterInfo != null)
                    {
                        masterInfo.node.activate(null);
                    }
                }
            });
        }
    }
}
