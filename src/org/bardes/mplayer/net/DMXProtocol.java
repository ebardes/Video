package org.bardes.mplayer.net;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum DMXProtocol
{
	SACN("Streaming ACN (E1.31)"),
	;
	
	private String description;

	private DMXProtocol(String description)
	{
		this.setDescription(description);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return this.description;
	}
}
