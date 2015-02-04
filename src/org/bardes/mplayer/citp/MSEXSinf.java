package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;
/*
    CITP_MSEX_1.2_SInf
    {
        CITP_MSEX_Header CITPMSEXHeader // CITP MSEX header. MSEX ContentType is "SInf". Version is at least 1.2 and
        // is the highest common version supported by both server and client.
        ucs1 UUID[36] // A standard 36 character UUID that uniquely identifies this media server.
        ucs2 ProductName[] // Display name of the product.
        uint8 ProductVersionMajor // Major version number of the product.
        uint8 ProductVersionMinor // Minor version number of the product.
        uint8 ProductVersionBugfix // Bugfix version number of the product.
        uint8 SupportedMSEXVersionsCount // Number of following MSEX version pairs.
        uint16 SupportedMSEXVersions[] // Each 2 byte value is MSB = major MSEX version, LSB = minor MSEX version.
        uint16 SupportedLibraryTypes // Bit‐encoded flagword that identifies which library
        // types are provided by the media server (e.g. this
        // would be 1 for Media, 2 for Effects, 4 for Cues etc.).
        uint8 ThumbnailFormatsCount // Number of following thumbnail format cookies.
        uint32 ThumbnailFormats[] // Must include "RGB8", but can also include "JPEG" and "PNG ".
        uint8 StreamFormatsCount // Number of following stream format cookies.
        uint32 StreamFormats[] // Must include "RGB8", but can also include "JPEG", "PNG ", "fJPG" and "fPNG".
        uint8 LayerCount // Number of following layer information blocks.
        LayerInformation
        {
        ucs1 DMXSource[] // DMX‐source connection string.
        }
    }
 */
public class MSEXSinf extends MSEXHeader
{
	public MSEXSinf()
	{
		super("SInf");
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		string(bc, "5268938F-2F9E-43F8-A899-74B13B6D055F");
		bc.putChar((char) 0); // Product name
		
		bc.put((byte) 0); // Version Major
		bc.put((byte) 1); // Version Minor 
		bc.put((byte) 0); // Version Bugfix
		
		bc.put((byte) 1); // Supported Versions
		bc.put((byte) 1); // Supported Version 1.2
		bc.put((byte) 2);
		
		bc.putShort((short) 1); // SupportedLibraryTypes
		
		bc.put((byte) 1);
		bc.put("RGB8".getBytes(), 0, 4); // ThumbnailFormats
		
		bc.put((byte) 1);
		bc.put("RGB8".getBytes(), 0, 4); // StreamFormats
		
		bc.put((byte) 0);
	}
}
