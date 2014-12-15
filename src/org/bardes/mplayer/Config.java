package org.bardes.mplayer;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement
public class Config
{
	private Set<GroupSlot> groups = new TreeSet<>();

	@XmlElementWrapper(name="groups")
	@XmlElement(name="group")
	public Set<GroupSlot> getGroups()
	{
		return groups;
	}

	public void setGroups(Set<GroupSlot> groups)
	{
		this.groups = groups;
	}

	public void addGroup(GroupSlot value)
	{
		this.groups.add(value);
	}

	public void save(File f)
	{
		JAXB.marshal(this, f);
	}

	public static Config load(File file)
	{
		return JAXB.unmarshal(file, Config.class);
	}
}
