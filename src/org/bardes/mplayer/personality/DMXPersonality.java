package org.bardes.mplayer.personality;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(String.class)
public enum DMXPersonality
{
	@XmlEnumValue("lite") LITE("Lite (24 addresses)"),
	@XmlEnumValue("regular") REGULAR("Regular"),
	;
	
	private String description;

	private DMXPersonality(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return description;
	}
}