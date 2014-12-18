package org.bardes.mplayer.sacn;


enum RootLayer implements Framing
{
	PREAMBLE_SIZE(2),
	POSTAMBLE_SIZE(2),
	ACN_PACKET_ID(12),
	RLP_FLAGS_AND_LENGTH(2),
	RLP_VERSION(4),
	CID_SENDER_ID(16),
	;
	
	int size;
	int offset;
	public static int bytes = 0;
	
	RootLayer(int size)
	{
		this.size = size;
	}
	
	static void init()
	{
		bytes = 0;
		RootLayer[] a = values();
		for (int i = 0; i < a.length; i++)
		{
			RootLayer x = a[i];
			x.offset = bytes;
			bytes += x.size;
		}
	}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public int getOffset()
	{
		return offset;
	}
}