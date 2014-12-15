package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="image")
public class ImageSlot extends Slot
{
	public ImageSlot()
	{
	}
	
	public ImageSlot(int id, String string, String href)
	{
		super(id, string, href);
	}

	@Override
	public String toString()
	{
		return String.format("Slot %03d - %s", id, description);
	}

	@Override
	@XmlTransient
	public Type getType()
	{
		return Type.IMAGE;
	}
}
