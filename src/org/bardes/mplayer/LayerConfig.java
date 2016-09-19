package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlTransient;

import org.bardes.mplayer.personality.DMXPersonality;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;


public class LayerConfig
{
	private int address = 0;
	private DMXPersonality personality;

	@Override
	public String toString()
	{
		return getAddress() + "/" + getPersonality();
	}
	
	public int getAddress()
	{
		return address;
	}

	public void setAddress(int address)
	{
		this.address = address;
	}

	public DMXPersonality getPersonality()
	{
		return personality;
	}
	
	public void setPersonality(DMXPersonality newValue)
	{
		personality = newValue;
	}

	@XmlTransient
	public ObservableValue<Integer> getAddressProperty()
	{
		return new ObservableValueBase<Integer>()
		{

			@Override
			public Integer getValue()
			{
				return address;
			}
		};
	}

	public ObservableValue<DMXPersonality> getPersonalityProperty()
	{
		return new ObservableValueBase<DMXPersonality>()
		{
			@Override
			public DMXPersonality getValue()
			{
				return personality;
			}
		};
	}
	
	public ObservableValue<Integer> getFootprintProperty()
	{
		return new ObservableValueBase<Integer>()
		{
			@Override
			public Integer getValue()
			{
				return personality.getFootprint();
			}
		};
	}
}
