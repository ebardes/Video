package org.bardes.mplayer;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;


public class Cue
{
	SimpleDoubleProperty cue = new SimpleDoubleProperty();
	SimpleDoubleProperty time = new SimpleDoubleProperty();
	SimpleStringProperty description = new SimpleStringProperty();
	
	public Cue()
	{
		this(0.0, "", 2.0);
	}
	
	public Cue(double cue, String description, double time)
	{
		setCue(cue);
		setDescription(description);
		setTime(time);
	}
	
	public void setTime(double time)
	{
		this.time.set(time);
	}
	
	public double getTime()
	{
		return this.time.get();
	}

	public void setDescription(String description)
	{
		this.description.set(description);
	}
	
	public String getDecription()
	{
		return this.description.get();
	}

	public double getCue()
	{
		return cue.get();
	}
	
	public void setCue(double cue)
	{
		this.cue.set(cue);
	}

}
