package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

@XmlRootElement(name="mediacue")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MediaCue extends Cue
{
	private String mediaURI;
	
	@XmlTransient
	private MediaPlayer mp;
	
	private void setMediaFile(String uri)
	{
		final Media m = new Media(uri);
		mp = new MediaPlayer(m);
		final MediaView mv = new MediaView(mp);
		
		final DoubleProperty width = mv.fitWidthProperty();
		final DoubleProperty height = mv.fitHeightProperty();
	
		width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
		mv.setPreserveRatio(true);

		setNode(mv);
	}

	public String getMediaURI()
	{
		return mediaURI;
	}

	public void setMediaURI(String mediaURI)
	{
		this.mediaURI = mediaURI;
		setMediaFile(mediaURI);
	}
	
	@Override
	public void play()
	{
		mp.setStartTime(new Duration(0));
		mp.play();
	}
}
