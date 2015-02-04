package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

/*
CITP_MSEX_VSrc
{
CITP_MSEX_Header CITPMSEXHeader // CITP MSEX header. MSEX ContentType is "VSrc".
uint16 SourceCount // Number of following source information blocks.
SourceInformation
{
uint16 SourceIdentifier // Source identifier.
ucs2 SourceName[] // Display name of the source (ie "Output 1", "Layer 2", "Camera 1" etc).
uint8 PhysicalOutput // If applicable, 0‐based index designating the physical video output index.
// Otherwise 0xFF.
uint8 LayerNumber // If applicable, 0‐based layer number, corresponding to the layers reported
// in the SInf message. Otherwise 0xFF.
uint16 Flags // Information flags:
// 0x0001 Without effects
uint16 Width // Full width.
uint16 Height // Full height.
}[]
}
*/
public class MSEXVServer extends MSEXHeader
{
    public MSEXVServer()
    {
        super("VSrc");
    }
    
    public static CITPHeader scan(ByteBuffer buffer)
    {
        return new MSEXVServer();
    }

    @Override
    public void stream(ByteBuffer bc)
    {
        super.stream(bc);
        
        bc.putShort((short) 1); // One Source
        
        string2(bc, "Live");
        
        bc.put((byte) -1); // physical output
        bc.put((byte) -1); // layer number
        
        bc.putShort((short) 1); // flags
        bc.putShort((short) 128); // width
        bc.putShort((short) 128); // height
    }
}
