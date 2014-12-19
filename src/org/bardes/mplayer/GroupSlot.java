package org.bardes.mplayer;

import java.util.Map;
import java.util.TreeMap;

import javafx.scene.Node;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class GroupSlot extends Slot
{
	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	
	Map<Integer, Slot> slots = new TreeMap<>();
	
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
		slots.put(item.id, item);
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

	public Slot get(int slot)
	{
		return slots.get(slot);
	}

}
