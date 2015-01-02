package org.bardes.mplayer.cue;

import java.io.File;
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
	private static final String FILENAME = "cuelist.xml";
	private TreeSet<Cue> cueList = new TreeSet<Cue>();

	public void save()
	{
		JAXB.marshal(this, FILENAME);
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

	public static CueStack load()
	{
		File f = new File(FILENAME);
		CueStack cueStack = null;
		if (f.canRead())
		{
			try
			{
				cueStack = JAXB.unmarshal(f, CueStack.class);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (cueStack == null)
		{
			cueStack = new CueStack();
			Cue zero = new Cue();
			zero.setId(BigDecimal.ONE);
			zero.setDescription("Default Cue");
			cueStack.cueList.add(zero);
		}
		return cueStack;
	}
}
