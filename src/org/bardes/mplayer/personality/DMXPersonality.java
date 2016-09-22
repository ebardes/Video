package org.bardes.mplayer.personality;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum(String.class)
public enum DMXPersonality
{
    @XmlEnumValue("master-lite") MASTER_LITE("Lite Master", MasterLitePersonality.class),
    @XmlEnumValue("master-regular") MASTER_REGULAR("Regular Master", MasterRegularPersonality.class),
	@XmlEnumValue("lite") LITE("Lite", LayerLitePersonality.class),
	@XmlEnumValue("regular") REGULAR("Regular", LayerRegularPersonality.class),
	@XmlEnumValue("large") LARGE("Large", LayerLargePersonality.class),
	@XmlEnumValue("hippo3_0_x") HIPPO("Loosely Green Hippo 3.0.x mapping", GreenHippoV3_0_x.class),
	@XmlEnumValue("arkaos") ARKAOS_V11_PRO("Loosely Arkaos Pro Layer V1.1", ArkosV11MediaLayer.class),
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
	    Personality personality = getInstance();
		return personality != null ? personality.getFootprint() : 0;
	}
	
	public Personality getInstance()
	{
        try
        {
            return klass.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	public String toString()
	{
		return description;
	}
}
