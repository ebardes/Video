package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@SuppressWarnings("restriction")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlSeeAlso({ ImageSlot.class, WebSlot.class, VideoSlot.class })
@XmlRootElement(name="slot")
public abstract class Slot implements Comparable<Slot>
{
	public enum Type
	{
		NEW,
		GROUP,
		IMAGE,
		VIDEO,
		WEB,
	}
	
	protected int id;
	
	private String description;

	protected String reference;

	protected int group;

	protected TreeItem<Slot> treeitem;
	
	protected boolean perserveAspectRatio = true;
	
	/**
	 * When content was uploaded. Used for synchronization.
	 */
	private long timestamp;
	
	public Slot()
	{
	}
	
	public Slot(int id, String string)
	{
		this.id = id;
		this.description = string;
	}

	public Slot(int id, String string, String reference)
	{
		this(id, string);
		this.setReference(reference);
	}

	@XmlElement(name="id", required=true)
	public int getSlot()
	{
		return id;
	}

	public void setSlot(int slot)
	{
		this.id = slot;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
	public int compareTo(Slot o)
	{
		return id - o.id;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Slot)
		{
			return id == ((Slot)obj).id;
		}
		return false;
	}

	public abstract Type getType();

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}

	public abstract Node getNode(Stage parent);
	public abstract Node getPreview(Node pane);
	public abstract Node getThumbNail();

	public boolean isPerserveAspectRatio()
	{
		return perserveAspectRatio;
	}

	public void setPerserveAspectRatio(boolean perserveAspectRatio)
	{
		this.perserveAspectRatio = perserveAspectRatio;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}
