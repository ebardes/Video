package org.bardes.mplayer;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class VSystem implements Controllable
{
	private List<Cue> cues = new ArrayList<Cue>();
	private int current;
	private StackPane root;
	private Show show;
	
	public VSystem(StackPane root)
	{
		this.root = root;
		current = -1;
	}

	
	private class KeyHander implements EventHandler<KeyEvent>
	{
		@Override
		public void handle(KeyEvent e)
		{
			if (e.getCode() == KeyCode.SPACE)
				go();
		}
	}

	public EventHandler<? super KeyEvent> getKeyHandler()
	{
		return new KeyHander();
	}

	@Override
	public void reset()
	{
	}

	@Override
	public void go()
	{
	}
}
