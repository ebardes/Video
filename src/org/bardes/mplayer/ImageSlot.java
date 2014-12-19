package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="image")
public class ImageSlot extends Slot
{
	private ImageView node;
	private ImageView thumbNail;

	public ImageSlot()
	{
	}
	
	public ImageSlot(int id, String string, String href)
	{
		super(id, string);
		setReference(href);
	}
	
	@Override
	public void setReference(String reference)
	{
		super.setReference(reference);
		node = new ImageView(reference);
		node.setPreserveRatio(false);
		
		thumbNail = new ImageView(reference);
		thumbNail.setFitHeight(128);
		thumbNail.setFitWidth(128);
		thumbNail.setPreserveRatio(true);
	}

	@Override
	public String toString()
	{
		return String.format("Slot %03d - %s", id, getDescription());
	}

	@Override
	@XmlTransient
	public Type getType()
	{
		return Type.IMAGE;
	}

	@Override
	public Node getNode()
	{
		return node;
	}

	@Override
	public Node getThumbNail()
	{
		return thumbNail;
	}
}
