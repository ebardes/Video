package org.bardes.mplayer;

import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="video")
public class VideoSlot extends Slot
{
    private MediaPlayer mp;
    
    public VideoSlot()
    {
    }
    
    @Override
    public void setReference(String reference)
    {
        super.setReference(reference);
        
        Media media = new Media(reference);
        mp = new MediaPlayer(media);
        mp.setAutoPlay(false);
    }
    
    @Override
    public String toString()
    {
    	return String.format("Slot %03d - %s", id, getDescription());
    }

    @Override
    public Type getType()
    {
        return Type.VIDEO;
    }

    @Override
    public Node getNode(Stage stage)
    {
        MediaView mediaView = new MediaView(mp);
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(stage.widthProperty().divide(4));
        mediaView.fitHeightProperty().bind(stage.heightProperty().divide(4));
        return mediaView;
    }
    
    @Override
    public Node getPreview(Node owner)
    {
    	MediaView mediaView = new MediaView(mp);
    	return mediaView;
    }

    @Override
    public Node getThumbNail()
    {
        MediaView mediaView = new MediaView(mp);
        mediaView.setPreserveRatio(true);
        mediaView.setFitHeight(128);
        mediaView.setFitWidth(128);
        return mediaView;
    }

}
