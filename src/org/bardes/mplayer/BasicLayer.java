package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import org.bardes.mplayer.Slot.Type;

public class BasicLayer implements Layer
{
	private BorderPane pane;
	private Slot slot;
	private Node node;
	private boolean running = false;

	public BasicLayer(BorderPane pane)
	{
		this.pane = pane;
	}
	
	private static double d(int n)
	{
		return (double)n / 255.0;
	}
	
	@Override
	public void setDimmer(int n)
	{
		pane.setOpacity(d(n));
		if (slot != null)
		{
		    if (slot.getType() == Type.VIDEO && node != null)
		    {
		        MediaView mv = (MediaView) node;
		        MediaPlayer mp = mv.getMediaPlayer();
		        if (n > 0 && !running)
		        {
		            mp.play();
    				running = true;
    			}
		        if (n == 0 && running)
		        {
		            mp.stop();
		            running = false;
		        }
		    }
		}
	}

	@Override
	public void setItem(int groupId, int slotId)
	{
		Config config = Main.getConfig();
		GroupSlot gs = config.getGroup(groupId);
		if (gs != null)
		{
			slot = gs.get(slotId);
			if (slot != null)
			{
				Node x = slot.getNode();
				if (x != null && x != node)
				{
					if (running && slot.getType() == Type.VIDEO)
					{
				        MediaView mv = (MediaView) node;
				        MediaPlayer mp = mv.getMediaPlayer();
				        mp.stop();
					}
				    node = x;
				    pane.setCenter(x);
    				pane.layout();
				}
			}
		}
	}

	@Override
	public void setVolume(int volume)
	{
		if (slot != null && slot.getType() == Type.VIDEO && node != null)
		{
			MediaView mv = (MediaView) node;
			MediaPlayer mp = mv.getMediaPlayer();
			mp.setVolume(d(volume));
		}
	}
}
