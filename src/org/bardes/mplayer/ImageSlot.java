package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="image")
public class ImageSlot extends Slot
{
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
		ImageView node = new ImageView(reference);
	    if (owner != null && node != null)
	    {
	        node.fitHeightProperty().bind(owner.heightProperty());
	        node.fitWidthProperty().bind(owner.widthProperty());
	        node.setPreserveRatio(perserveAspectRatio);
	    }
		return node;
	}

	@Override
	public Node getThumbNail()
	{
		ImageView thumbNail = new ImageView(reference);
		thumbNail.setFitHeight(128);
		thumbNail.setFitWidth(128);
		thumbNail.setSmooth(true);
		thumbNail.setPreserveRatio(perserveAspectRatio);
		return thumbNail;
	}

	@Override
	public Node getPreview(Node in)
	{
		ImageView preview = new ImageView(reference);
	    if (in != null && in instanceof Pane && preview != null)
	    {
	    	preview.setPreserveRatio(perserveAspectRatio);
	    	Pane owner = (Pane) in;
	    	preview.fitHeightProperty().bind(owner.maxHeightProperty());
	    	preview.fitWidthProperty().bind(owner.maxWidthProperty());
//	    	preview.autosize();
	    }
		return preview;
	}
}
