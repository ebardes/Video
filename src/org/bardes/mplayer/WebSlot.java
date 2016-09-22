package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="web")
public class WebSlot extends Slot
{
	private WebView webView;
	private WebView webViewThumb;
	private WebView preview;

	public WebSlot()
	{
	}
	
	public WebSlot(int id, String description, String url)
	{
		super(id, description, url);
	}
	
	@Override
	public void setReference(String url)
	{
		super.setReference(url);
	}
	
	@Override
	public String toString()
	{
		return String.format("Slot %03d - %s", id, getDescription());
	}

	@Override
	public Type getType()
	{
		return Type.WEB;
	}

	@Override
	public Node getNode(Stage owner)
	{
		return webView;
	}

	@Override
	public Node getThumbNail()
	{
		return webViewThumb;
	}
	
	@Override
	public Node getPreview(Node owner)
	{
		return preview;
	}
}
