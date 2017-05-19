package org.bardes.mplayer.personality;

import java.nio.ByteBuffer;

/**
 * @author eric
 * An abstraction of a thing that can receive DMX frames
 */
public interface DMXReceiver
{
    /**
     * @param frame A buffer of 512 bytes representing a DMX frame.
     */
    void onFrame(ByteBuffer frame);
}
