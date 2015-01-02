package org.bardes.mplayer.cue;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="stack")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CueStack
{
	private TreeSet<Cue> cueList = new TreeSet<Cue>();

	public void save()
	{
		JAXB.marshal(this, "cuelist.xml");
	}
	
	public void go(BigDecimal id)
	{
		
	}
	
	@XmlElementWrapper(name="cues")
	@XmlElement(name="cue")
	public Set<Cue> getCueList()
	{
		return cueList;
	}

	public void setCueList(Set<Cue> cueList)
	{
		this.cueList = new TreeSet<Cue>(cueList);
	}
}
