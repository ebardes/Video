package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

/*
CITP_MSEX_1.2_ELIn
{
CITP_MSEX_Header CITPMSEXHeader // CITP MSEX header. MSEX ContentType is "ELIn" and version is 1.2.
uint8 LibraryType // Content type requested.
uint16 LibraryCount // Number of following element library information blocks.
ElementLibraryInformation
{
MSEXLibraryId Id // Library id.
uint32 SerialNumber // See below
uint8 DMXRangeMin // DMX range start value.
uint8 DMXRangeMax // DMX range end value.
ucs2 Name[] // Library name.
uint16 LibraryCount // Number of sub libraries in the library (0‐256).
uint16 ElementCount // Number of elements in the library (0‐256).
}[]
}
 */
public class MSEXElinMessage extends MSEXHeader
{
    public MSEXElinMessage()
    {
        super("ELIn");
    }
    
    @Override
    public void stream(ByteBuffer bc)
    {
        super.stream(bc);
        
        bc.put((byte) 1); // Library Type
        bc.putShort((short) 0); // Library Count
    }
}
