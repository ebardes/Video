package org.bardes.mplayer.cue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Cue implements Comparable<Cue>
{
	private BigDecimal id;
	
	private BigDecimal inTime;

	private BigDecimal outTime;

	private BigDecimal inDelayTime;

	private BigDecimal outDelayTime;
	
	private String description;
	
	private List<CueLayer> layer = new ArrayList<>(4);
	
	@Override
	public int compareTo(Cue o)
	{
		return getId().compareTo(o.getId());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Cue)
		{
			Cue c = (Cue) obj;
			return id.equals(c.id);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return id.toPlainString() + " " + description;
	}

	@XmlAttribute
	public BigDecimal getId()
	{
		return id;
	}

	public void setId(BigDecimal cue)
	{
		this.id = cue;
	}

	@XmlAttribute
	public BigDecimal getInTime()
	{
		return inTime;
	}

	public void setInTime(BigDecimal inTime)
	{
		this.inTime = inTime;
	}

	@XmlAttribute
	public BigDecimal getOutTime()
	{
		return outTime;
	}

	public void setOutTime(BigDecimal outTime)
	{
		this.outTime = outTime;
	}

	@XmlAttribute
	public BigDecimal getInDelayTime()
	{
		return inDelayTime;
	}

	public void setInDelayTime(BigDecimal inDelayTime)
	{
		this.inDelayTime = inDelayTime;
	}

	@XmlAttribute
	public BigDecimal getOutDelayTime()
	{
		return outDelayTime;
	}

	public void setOutDelayTime(BigDecimal outDelayTime)
	{
		this.outDelayTime = outDelayTime;
	}

	@XmlAttribute
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}

	@XmlElementWrapper(name="layers")
	@XmlElement(name="layer")
	public List<CueLayer> getLayer()
	{
		return layer;
	}

	public void setLayer(List<CueLayer> layer)
	{
		this.layer = layer;
	}
	
	public CueLayer layer(int n)
	{
		return this.layer(n);
	}

}
