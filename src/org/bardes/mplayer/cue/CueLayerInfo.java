package org.bardes.mplayer.cue;

import org.bardes.mplayer.GroupSlot;
import org.bardes.mplayer.Slot;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

@SuppressWarnings("restriction")
public class CueLayerInfo
{
	public AnchorPane ap;

	public Label dimmerLabel;

	public Label volumeLabel;

	public Slider dimmer;

	public Slider volume;

	public int dimmerValue;

	public int volumeValue;

	public ChoiceBox<GroupSlot> group;
	
	public ChoiceBox<Slot> slot;
	
	public Pane cueView;
	
	private boolean touched = false;
	
	public void touch()
	{
	    if (!touched)
	    {
    	    ap.getStyleClass().clear();
    	    ap.getStyleClass().add("modified");
    	    touched = true;
    	    
    	    Button release = (Button) ap.lookup("#cueRelease");
    	    release.setDisable(false);
	    }
	}

	public void setDimmerLevel(Number newValue)
	{
		int val = newValue.intValue();
		String s = String.format("%d%%", val);
		dimmerValue = val;
		dimmerLabel.setText(s);
		touch();
	}

	public void setVolumeLevel(Number newValue)
	{
		int val = newValue.intValue();
		String s = String.format("%d%%", val);
		volumeValue = val;
		volumeLabel.setText(s);
		touch();
	}

    public void changeGroup(GroupSlot groupSlot)
    {
        slot.getItems().setAll(groupSlot.slots.values());
        slot.getSelectionModel().selectFirst();
        touch();
    }

    public void changeSlot(Slot newValue)
    {
        Slot s = slot.getValue();
        cueView.getChildren().clear();
        if (s != null)
        {
            cueView.getChildren().add(s.getThumbNail());
        }
    }
}
