package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import org.bardes.mplayer.Slot.Type;

public class BasicLayer implements Layer
{
	private BorderPane pane;
	private Slot slot;
	private Node node;
	private boolean running = false;
    private int layerId;
    private int dimmer = -1;
    private int lastGroup = -1;
    private int lastSlot = -1;
    private MediaPlayer mp;
    private Stage stage;
    private boolean debugging;

	public BasicLayer(int layerId, Stage stage, BorderPane pane)
	{
		this.layerId = layerId;
        this.stage = stage;
        this.pane = pane;
	}
	
	private static double d(int n)
	{
		return (double)n / 255.0;
	}
	
	@Override
	public void setDimmer(int n)
	{
	    if (dimmer == n)
	        return;
	    
	    if (debugging)
	        System.out.println("Dimmer="+n+" " + layerId);
	    this.dimmer = n;
		pane.setOpacity(d(n));
		if (slot != null)
		{
		    if (slot.getType() == Type.VIDEO && node != null)
		    {
		        if (n > 0 && !running)
		        {
		            start(node, "A");
    			}
		        if (n == 0 && running)
		        {
		            stop("A");
		        }
		    }
		}
	}

	@Override
	public void setItem(int groupId, int slotId)
	{
	    if (groupId == lastGroup && slotId == lastSlot)
	        return;
	    
	    lastGroup = groupId;
	    lastSlot = slotId;
	    
	    if (debugging)
	        System.out.format("setItem(%d, %d) %d%n", groupId, slotId, layerId);
	        
        if (running && dimmer > 0 && node instanceof MediaView)
        {
            stop("B");
        }
        
		Config config = Main.getConfig();
		GroupSlot gs = config.getGroup(groupId);
		if (gs != null)
		{
			slot = gs.get(slotId);
			if (slot != null)
			{
				Node x = slot.getNode(stage);
				if (x != null)
				{
				    node = x;
				    pane.setCenter(x);
    				
    				if (slot.getType() == Type.VIDEO && dimmer > 0 && !running)
    				{
				        start(x, "B");
    				}
				}
			}
		}
	}

	/**
	 * @param mp
	 */
	protected void start(Node node, String debug)
	{
	    if (debugging)
	        System.out.println("Start "+layerId+" " + debug);
		MediaView mv = (MediaView) node;
		MediaPlayer mp = mv.getMediaPlayer();
		mp.play();
        this.mp = mp;
		running = true;
	}

	/**
	 * @param mp
	 */
	protected void stop(String debug)
	{
	    if (debugging)
	        System.out.println("Stop "+layerId+" " + debug);
		if (mp != null)
		{
		    mp.stop();
		    mp = null;
		}
		running = false;
	}

	@Override
	public void setVolume(int volume)
	{
		if (mp != null)
		{
			mp.setVolume(d(volume));
		}
	}
}
