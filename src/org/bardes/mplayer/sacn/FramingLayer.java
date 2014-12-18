package org.bardes.mplayer.sacn;

enum FramingLayer implements Framing
{
	EFL_FLAGS_AND_LENGTH(2),
	EFL_VERSION(4),
	SOURCE(64),
	PRIORITY(1),
	RESERVED(2),
	SEQ_NUM(1),
	EFL_OPTIONS(1),
	UNIVERSE(2),
	;
	
	private int size;
	private int offset;
	public static int bytes = 0;

	private FramingLayer(int size)
	{
		this.size = size;
	}

	static void init()
	{
		bytes = 0;
		FramingLayer[] a = values();
		for (int i = 0; i < a.length; i++)
		{
			FramingLayer x = a[i];
			x.offset = bytes;
			bytes += x.size;
		}
	}

	public int getSize()
	{
		return size;
	}

	public int getOffset()
	{
		return offset;
	}
}
