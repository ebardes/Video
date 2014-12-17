package org.bardes.mplayer;

import java.util.Set;
import java.util.TreeSet;

import javafx.scene.Node;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class GroupSlot extends Slot
{
	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	Set<Slot> slots = new TreeSet<>();
	
	public GroupSlot()
	{
	}
	
	public GroupSlot(int i, String string)
	{
		super(i, string);
	}
	
	@Override
	public String toString()
	{
		return String.format("%03d - %s", id, getDescription());
	}

	public void addItem(Slot item)
	{
	    item.group = id;
		slots.add(item);
	}

	@Override
	public Type getType()
	{
		return Type.GROUP;
	}

	@Override
	public Node getNode()
	{
		return null;
	}

	@Override
	public Node getThumbNail()
	{
		return null;
	}

}
