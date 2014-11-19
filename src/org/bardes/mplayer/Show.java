package org.bardes.mplayer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="show")
@XmlAccessorType(XmlAccessType.FIELD)
public class Show
{
	public String images;
	
	public String baseDirectory;
	
	public int projectors = 1;
}
