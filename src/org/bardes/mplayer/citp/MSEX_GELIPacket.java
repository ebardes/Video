package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public class MSEX_GELIPacket extends MSEXHeader
{
    public MSEX_GELIPacket()
    {
        super("GELI");
    }

    public static CITPHeader scan(ByteBuffer buffer)
    {
        return new MSEXElinMessage();
    }
}
