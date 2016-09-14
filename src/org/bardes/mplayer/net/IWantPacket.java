package org.bardes.mplayer.net;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="iwant")
public class IWantPacket
{

	private int group;
	private int slot;

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

}
