package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author eric
 * The Generic Slot. Specific types of media will extend this class.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlSeeAlso({ ImageSlot.class, WebSlot.class, VideoSlot.class })
@XmlRootElement(name="slot")
public abstract class Slot implements Comparable<Slot>
{
	protected int id;
	
	private String description;

	protected String reference;
	
	private String page;
	
	protected String previewImage;

	protected int group;

	protected TreeItem<Slot> treeitem;
	
	protected boolean perserveAspectRatio = true;
	
	/**
	 * When content was uploaded. Used for synchronization.
	 */
	private long timestamp;

	private long length;
	
	private String contentType;
	
	/**
	 * No Arg Construction. Only used for JAXB.
	 */
	public Slot()
	{
	}
	
	protected Slot(int id, String string)
	{
		this.id = id;
		this.description = string;
	}

	protected Slot(int id, String string, String reference)
	{
		this(id, string);
		this.setReference(reference);
	}

	/**
	 * Getter for the Slot ID.
	 * @return The Slot ID
	 */
	@XmlElement(name="id", required=true)
	public int getSlot()
	{
		return id;
	}

	/**
	 * Setter for the Slot ID.
	 * @param slot The Slot ID. In the case of GroupSlot, it's the group id.
	 */
	public void setSlot(int slot)
	{
		this.id = slot;
	}

	/**
	 * Getter for the human friendly description.
	 * @return A human friendly description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Setter for the human friendly description.
	 * @param description A human friendly description.
	 */
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

	/**
	 * @return The type of the Slot.
	 */
	public abstract Type getType();

	/**
	 * @return Local storage of the slot data.
	 */
	public String getReference()
	{
		return reference;
	}

	/**
	 * @param reference Local storage of the slot data
	 */
	public void setReference(String reference)
	{
		this.reference = reference;
	}

	/**
	 * @param parent
	 * @return A JavaFX Node 
	 */
	public abstract Node getNode(Stage parent);
	public abstract Node getPreview(Node pane);
	public abstract Node getThumbNail();
	
	/**
	 * @return
	 */
	@XmlTransient
	public File getStorage()
	{
		String ext = "";
		int n = reference.lastIndexOf('.');
		if (n > 0)
			ext = reference.substring(n);
		
		String directory = "/tmp"; // Main.getConfig().getWorkDirectory();
		File file = new File(directory);
		file = new File(file, String.format("group_%03d", group));
		if (!file.exists())
			file.mkdirs();
		file = new File(file, String.format("slot_%03d%s", id, ext));
		return file;
	}

	/**
	 * Not used.
	 * @return Whether to preserve the aspect ratio of the content.
	 */
	public boolean isPerserveAspectRatio()
	{
		return perserveAspectRatio;
	}

	/**
	 * @param perserveAspectRatio Whether to preserve the aspect ratio of the content.
	 */
	public void setPerserveAspectRatio(boolean perserveAspectRatio)
	{
		this.perserveAspectRatio = perserveAspectRatio;
	}

	/**
	 * @return The Asset Timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp The Asset Timestamp
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @return The content size in bytes
	 */
	public long getLength()
	{
		return length;
	}

	/**
	 * @param length The content size in bytes
	 */
	public void setLength(long length)
	{
		this.length = length;
	}

	/**
	 * @return The Group Number. Not applicable for GroupSlot types
	 */
	public int getGroup()
	{
		return group;
	}

	/**
	 * @param group The Group Number. Not applicable for GroupSlot types
	 */
	public void setGroup(int group)
	{
		this.group = group;
	}

	public String getPreviewImage()
	{
		return previewImage;
	}

	public void setPreviewImage(String previewImage)
	{
		this.previewImage = previewImage;
	}
	
	@XmlElement
	public String getDatestring()
	{
		return new Date(timestamp).toString();
	}

	public void makeThumbNail(Parent parent)
	{
	}

	/**
	 * @return The RFC 2046 MIME Content Type
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @param contentType The RFC 2046 MIME Content Type
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getPage()
	{
		return page;
	}

	public void setPage(String page)
	{
		this.page = page;
	}
}
