package org.bardes.mplayer.cue;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public class CueLayerInfo
{
	public AnchorPane ap;

	public Label dimmerLabel;

	public Label volumeLabel;

	public Slider dimmer;

	public Slider volume;

	public int dimmerValue;

	public int volumeValue;

	private boolean changed = false;

	public void setDimmerLevel(Number newValue)
	{
		int val = newValue.intValue();
		String s = String.format("%d%%", val);
		dimmerValue = val;
		dimmerLabel.setText(s);
		changed = true;
	}

	public void setVolumeLevel(Number newValue)
	{
		int val = newValue.intValue();
		String s = String.format("%d%%", val);
		volumeValue = val;
		volumeLabel.setText(s);
		changed = true;
	}
}
