package org.bardes.mplayer.net;

import javax.xml.bind.annotation.XmlRootElement;

import org.bardes.mplayer.GroupSlot;
import org.bardes.mplayer.Slot;

@XmlRootElement(name="ihave")
public class IHavePacket
{
	private int group;
	private int slot;
	private long timestamp;
	private long now;
	
	public IHavePacket()
	{
	}

	public IHavePacket(GroupSlot group, Slot item)
	{
		this.group = group.getSlot();
		this.slot = item.getSlot();
		this.timestamp = item.getTimestamp();
		this.now = System.currentTimeMillis();
	}

	public int getGroup()
	{
		return group;
	}

	public void setGroup(int group)
	{
		this.group = group;
	}

	public int getSlot()
	{
		return slot;
	}

	public void setSlot(int slot)
	{
		this.slot = slot;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public long getNow()
	{
		return now;
	}

	public void setNow(long now)
	{
		this.now = now;
	}
}
