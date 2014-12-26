package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="web")
public class WebSlot extends Slot
{
	private WebView webView;
	private WebView webViewThumb;
	private WebView preview;

	public WebSlot()
	{
	}
	
	public WebSlot(int i, String description, String reference)
	{
		super(i, description, reference);
	}
	
	@Override
	public void setReference(String reference)
	{
		super.setReference(reference);
		
		webView = new WebView();
		webView.getEngine().loadContent(reference);
		
		preview = new WebView();
		
		webViewThumb = new WebView();
		webViewThumb.getEngine().loadContent(reference);
		webViewThumb.setMaxWidth(128);
		webViewThumb.setMaxHeight(128);
		webViewThumb.setDisable(true);
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
	public Node getPreview()
	{
		return preview;
	}
}
