package org.bardes.mplayer;

import org.bardes.mplayer.Slot.Type;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class BasicLayer implements Layer
{
	private Pane pane;
	private Slot slot;
	private Node node;

	public BasicLayer(Pane pane)
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
	}

	@Override
	public void setItem(int groupId, int slotId)
	{
		ObservableList<Node> children = pane.getChildren();
		children.clear();
		node = null;
		
		Config config = Main.getConfig();
		GroupSlot gs = config.getGroup(groupId);
		if (gs != null)
		{
			slot = gs.get(slotId);
			if (slot != null)
			{
				node = slot.getNode();
				children.add(node);
			}
		}
	}

	@Override
	public void setVolume(int volume)
	{
		if (slot.getType() == Type.VIDEO && node != null)
		{
			MediaView mv = (MediaView) node;
			MediaPlayer mp = mv.getMediaPlayer();
			mp.setVolume(d(volume));
		}
	}
}
