package org.bardes.mplayer.personality;

import java.util.List;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.cue.CueStack;
import org.bardes.mplayer.net.NetworkListener;

public class InternalListener implements NetworkListener
{
	private Personality personality;
	private CueStack stack;
	private List<Layer> layers;
	
	public InternalListener(CueStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void start()
	{
		layers = Main.layers;
	}

	@Override
	public void stop()
	{
	}

	@Override
	public boolean isReceiving()
	{
		return true;
	}

	@Override
	public void setPersonality(Personality personality)
	{
		this.personality = personality;
	}

	@Override
	public Personality getPersonality()
	{
		return personality;
	}

}
