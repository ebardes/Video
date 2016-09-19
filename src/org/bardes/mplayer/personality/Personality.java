package org.bardes.mplayer.personality;

import java.nio.ByteBuffer;

import org.bardes.mplayer.Layer;

public interface Personality
{
	int getFootprint();
	
	/**
	 * Decode the DMX byte stream.
	 * @param dmxStream
	 */
	void decode(ByteBuffer dmxStream);
	
	/**
	 * Make changes in the presentation layer. Runs in the appropriate thread for JavaFX actions.
	 */
	void activate(Layer layer);
}
