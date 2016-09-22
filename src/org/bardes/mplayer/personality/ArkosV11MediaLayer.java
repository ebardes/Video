package org.bardes.mplayer.personality;

import static org.bardes.mplayer.sacn.N.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bardes.mplayer.Layer;

public class ArkosV11MediaLayer implements Personality
{

	private int dimmer;
	private int slot;
	private int group;
	private int xScale;
	private int yScale;
	private int rotate;
	private int yShift;
	private int xShift;

	@Override
	public int getFootprint()
	{
		return 43;
	}

	@Override
	public void decode(ByteBuffer d)
	{
		int start = d.position() - 1;
		d.order(ByteOrder.BIG_ENDIAN);
		
		dimmer = u(d.get());
		group = u(d.get());
		slot = u(d.get());
		
		d.position(start + 27);
		xScale = us(d.getShort());
		yScale = us(d.getShort());
		
		d.position(start + 35);
		rotate = us(d.getShort());
		xShift = us(d.getShort());
		yShift = us(d.getShort());
	}

	@Override
	public void activate(Layer layer)
	{
		layer.setItem(group, slot);
		layer.setDimmer(dimmer);
		layer.shift(xShift, yShift, xScale, yScale, rotate);
	}
}
