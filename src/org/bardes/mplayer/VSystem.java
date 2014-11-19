package org.bardes.mplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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

	/**
	 * Take the next cue. Wrap around as needed.
	 */
	public void go()
	{
		if (cues.isEmpty())
			return;
		
		int n = current+1;
		if (n >= cues.size())
			n = 0;
		
		Cue next = cues.get(n);
		go(next);
	}

	private void go(Cue next)
	{
		int n = cues.indexOf(next);
		if (n == current)
			return;
		
		next = cues.get(n);
		Duration time = new Duration(next.getTime());
		
		if (current >= 0)
		{
			Cue cue = cues.get(current);
			FadeTransition ft2 = new FadeTransition(time, cue.getNode());
			ft2.setToValue(0.0);
			ft2.play();
		}
		
		FadeTransition ft1 = new FadeTransition(time, next.getNode());
		ft1.setFromValue(0.0);
		ft1.setToValue(1.0);
		ft1.play();
		
		next.play();
		
		current = n;
	}

	@Override
	public void reset()
	{
		ObservableList<Node> nodes = root.getChildren();
		nodes.clear();
		
		try
		{
			show = XML.loadShow("show.xml");
		}
		catch (Exception e)
		{
			show = new Show();
			show.baseDirectory = ".";
			XML.saveShow(show, "show.xml");
		}
		
		File dir = new File(show.baseDirectory);
		File[] files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name)
			{
				return name.startsWith("cue");
			}});
		
		for (File f : files)
		{
			Cue c = XML.loadCue(f);
			cues.add(c);
			root.getChildren().add(c.getNode());
		}
		Collections.sort(cues);
	}

	public static Cue mediaCue(final File f)
	{
		
		MediaCue cue = new MediaCue();
		cue.setMediaURI(f.toURI().toString());
		
		return cue;
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

	public void addCue(Cue cue)
	{
		XML.saveCue(cue, new File(show.baseDirectory));
		cues.add(cue);
		Collections.sort(cues);
	}
}
