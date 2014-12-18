package org.bardes.mplayer;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
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

	public GroupSlot getGroup(int group)
	{
		for (GroupSlot g : groups)
		{
			if (g.id == group)
				return g;
		}
		return null;
	}

	/**
	 * Builds a whole new Config. Usually first-run or recovery. 
	 * 
	 * @param location
	 * @return
	 * @throws URISyntaxException
	 */
    public static Config reset(URL location) throws URISyntaxException
    {
        Config config = new Config();
        GroupSlot system = new GroupSlot(0, "System");
        config.addGroup(system);
        for (int i = 1; i <= 16; i++)
            config.addGroup(new GroupSlot(i, "User"));
        
        
        File f = new File(location.toURI());
        f = f.getParentFile();
        f = new File(f, "images/system");
        File[] pngFiles = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.toString().toLowerCase().endsWith(".png");
            }
        });
        
        int id = 1;
        for (File p : pngFiles)
        {
            String name = p.getName();
            name = name.replace(".png", "");
			ImageSlot is = new ImageSlot(id++, name, p.toURI().toString());
            system.addItem(is);
        }
        
        return config;
    }
}
