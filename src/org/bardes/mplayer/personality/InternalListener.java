package org.bardes.mplayer.personality;

import java.util.List;

import org.bardes.mplayer.Layer;
import org.bardes.mplayer.cue.CueStack;
import org.bardes.mplayer.net.NetworkListener;

public class InternalListener implements NetworkListener
{
	private CueStack stack;
	private List<Layer> layers;
    private DMXReceiver receiver;
	
	public InternalListener(CueStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void start()
	{
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
    public void setReceiver(DMXReceiver receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public DMXReceiver getReceiver()
    {
        return receiver;
    }
}
