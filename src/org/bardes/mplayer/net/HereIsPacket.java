package org.bardes.mplayer.net;

import javax.xml.bind.annotation.XmlRootElement;

import org.bardes.mplayer.Slot;

@XmlRootElement(name="reply")
public class HereIsPacket
{
	boolean success;
	
	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public Slot getSlot()
	{
		return slot;
	}

	public void setSlot(Slot slot)
	{
		this.slot = slot;
	}

	Slot slot;
}
