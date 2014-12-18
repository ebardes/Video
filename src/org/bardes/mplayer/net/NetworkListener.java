package org.bardes.mplayer.net;

import org.bardes.mplayer.personality.Personality;

public interface NetworkListener
{
	void start();
	void stop();
	boolean isReceiving();
	
	void setPersonality(Personality personality);
	Personality getPersonality();
}
