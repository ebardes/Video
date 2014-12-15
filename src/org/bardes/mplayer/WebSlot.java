package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="web")
public class WebSlot extends Slot
{
	public WebSlot()
	{
	}
	
	public WebSlot(int i, String description, String reference)
	{
		super(i, description, reference);
	}
	
	@Override
	public String toString()
	{
		return String.format("Slot %03d - %s", id, description);
	}

	@Override
	public Type getType()
	{
		return Type.WEB;
	}
}
