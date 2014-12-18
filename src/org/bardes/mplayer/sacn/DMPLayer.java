package org.bardes.mplayer.sacn;

enum DMPLayer implements Framing
{
	DL_FLAGS_AND_LENGTH(2),
	DL_VERSION(1),
	DL_ADDR_TYPE(1),
	FIRST_ADDR(2),
	ADDR_INC(2),
	DMX_SIZE(2),
	DMX_START_CODE(1),
	DMX_DATA(512),
	;
	
	private int size;
	private int offset;
	public static int bytes = 0;

	static void init()
	{
		bytes = 0;
		DMPLayer[] a = values();
		for (int i = 0; i < a.length; i++)
		{
			DMPLayer x = a[i];
			x.offset = bytes;
			bytes += x.size;
		}
	}

	private DMPLayer(int size)
	{
		this.size = size;
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
