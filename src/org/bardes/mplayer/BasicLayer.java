package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
		if (n > 0 && !running)
		{
		    if (slot != null)
		    {
    			if (slot.getType() == Type.VIDEO && node != null)
    			{
    				MediaView mv = (MediaView) node;
    				MediaPlayer mp = mv.getMediaPlayer();
    				mp.play();
    				running = true;
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
				    node = x;
				    pane.setCenter(x);
				    if (x instanceof ImageView)
				    {
				        ImageView iv = (ImageView) x;
				        iv.setFitHeight(pane.getHeight());
				        iv.setFitWidth(pane.getWidth());
				    }
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
