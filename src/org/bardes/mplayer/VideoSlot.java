package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlType;

import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

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
    }

    @Override
    public Type getType()
    {
        return Type.VIDEO;
    }

    @Override
    public Node getNode()
    {
        return new MediaView(mp);
    }

    @Override
    public Node getThumbNail()
    {
        return new MediaView(mp);
    }

}
