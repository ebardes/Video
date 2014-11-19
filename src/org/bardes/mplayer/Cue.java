package org.bardes.mplayer;

import java.math.BigDecimal;

import javafx.scene.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class Cue implements Comparable<Cue>
{
	private BigDecimal cue;
	private double time = 1.0;
	
	private Node node;
	
	@Override
	public int compareTo(Cue o)
	{
		return cue.compareTo(o.cue);
	}

	public BigDecimal getCue()
	{
		return cue;
	}

	public void setCue(BigDecimal cue)
	{
		this.cue = cue;
	}

	@XmlTransient
	public Node getNode()
	{
		return node;
	}

	public void setNode(Node node)
	{
		this.node = node;
	}

	public double getTime()
	{
		return time;
	}

	public void setTime(double time)
	{
		this.time = time;
	}

	public void play()
	{
	}
	
	@Override
	public String toString()
	{
		return "Cue "+cue;
	}
}
