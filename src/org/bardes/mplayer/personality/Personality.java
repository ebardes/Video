package org.bardes.mplayer.personality;

import java.nio.ByteBuffer;

public interface Personality
{
	int getFootprint();
	void process(ByteBuffer dmxStream);
}
