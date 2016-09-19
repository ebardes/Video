package org.bardes.mplayer.personality;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(String.class)
public enum DMXPersonality
{
	@XmlEnumValue("lite") LITE("Lite", LayerLitePersonality.class),
	@XmlEnumValue("regular") REGULAR("Regular", LayerRegularPersonality.class),
	@XmlEnumValue("large") LARGE("Large", LayerLargePersonality.class),
	@XmlEnumValue("hippo3_0_x") HIPPO("Loosely Green Hippo 3.0.x mapping", GreenHippoV3_0_x.class)
	;
	
	private String description;
	private Class<? extends Personality> klass;

	private DMXPersonality(String description, Class<? extends Personality> klass)
	{
		this.description = description;
		this.klass = klass;
	}
	
	public int getFootprint()
	{
		try
		{
			return klass.newInstance().getFootprint();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return description;
	}
}
