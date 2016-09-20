package org.bardes.mplayer.net;

import org.bardes.mplayer.personality.DMXReceiver;

public interface NetworkListener
{
	void start();
	void stop();
	boolean isReceiving();
	
	void setReceiver(DMXReceiver receiver);
	DMXReceiver getReceiver();
}
