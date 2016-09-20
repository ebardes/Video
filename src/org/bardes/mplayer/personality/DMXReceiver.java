package org.bardes.mplayer.personality;

import java.nio.ByteBuffer;

public interface DMXReceiver
{
    void onFrame(ByteBuffer frame);
}
