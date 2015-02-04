package org.bardes.mplayer.citp;

import java.nio.ByteBuffer;

public abstract class MSEXHeader extends CITPHeader
{
	private String type;

	public MSEXHeader(String type)
	{
		this.type = type;
		setContentType("MSEX");
	}
	
	@Override
	public void stream(ByteBuffer bc)
	{
		super.stream(bc);
		
		bc.put((byte) 1);
		bc.put((byte) 1);
		bc.put(type.getBytes(), 0, 4);
	}

    public static CITPHeader scan(ByteBuffer buffer)
    {
        byte[] data = buffer.array();
        String msexType = new String(data, 2, 4);
        System.out.println("MSEXSubtype: " + msexType);
        buffer.position(6);
        
        CITPHeader ret = null;
        if (msexType.equalsIgnoreCase("RqSt"))
        {
            ret = MSEXVStream.scan(buffer);
        }
        if (msexType.equalsIgnoreCase("GVSr"))
        {
            ret = MSEXVServer.scan(buffer);
        }
        else if (msexType.equalsIgnoreCase("GELI"))
        {
            ret = MSEX_GELIPacket.scan(buffer);
        }
        
        return ret;
    }
}
