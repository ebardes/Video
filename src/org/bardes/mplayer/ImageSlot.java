package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="image")
public class ImageSlot extends Slot
{
	private ImageView node;
	private ImageView thumbNail;
	private ImageView preview;

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
		preview = new ImageView(reference);
		
		thumbNail = new ImageView(reference);
		thumbNail.setFitHeight(128);
		thumbNail.setFitWidth(128);
		thumbNail.setSmooth(true);
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
	public Node getNode(Stage owner)
	{
	    if (owner != null)
	    {
	        node.fitHeightProperty().bind(owner.heightProperty().divide(4));
	        node.fitWidthProperty().bind(owner.widthProperty().divide(4));
	    }
	    node.setPreserveRatio(perserveAspectRatio);
		return node;
	}

	@Override
	public Node getThumbNail()
	{
		thumbNail.setPreserveRatio(perserveAspectRatio);
		return thumbNail;
	}

	@Override
	public Node getPreview(Node in)
	{
		preview.setPreserveRatio(perserveAspectRatio);
	    if (in != null && in instanceof Pane)
	    {
	    	Pane owner = (Pane) in;
	    	preview.fitHeightProperty().bind(owner.heightProperty());
	    	preview.fitWidthProperty().bind(owner.widthProperty());
	    	preview.autosize();
	    }
		return preview;
	}
}
