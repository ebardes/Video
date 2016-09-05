package org.bardes.mplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.bardes.mplayer.net.DMXProtocol;
import org.bardes.mplayer.personality.DMXPersonality;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement
public class Config
{
	static File location;

    private boolean debugEnabled;
	
	private int universe = 1;
	
	private int offset = 1;
	
	private Set<GroupSlot> groups = new TreeSet<>();

	private HWXY displayPosition;
	
	private HWXY editorPosition;
	
	private DMXPersonality dmxPersonality = DMXPersonality.LITE;
	
	private DMXProtocol dmxProtocol = DMXProtocol.SACN;

	private String networkInterface;

    private ScreenInfo screenInfo;
    
    private String workDirectory;

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

	public void save()
	{
		JAXB.marshal(this, location);
	}

	public static Config load(File file)
	{
		location = file;
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

	@XmlElement(name="personality")
	public DMXPersonality getDmxPersonality()
	{
		return dmxPersonality;
	}

	public void setDmxPersonality(DMXPersonality personality)
	{
		this.dmxPersonality = personality;
	}

	@XmlElement(name="protocol")
	public DMXProtocol getDmxProtocol()
	{
		return dmxProtocol;
	}

	public void setDmxProtocol(DMXProtocol dmxProtocol)
	{
		this.dmxProtocol = dmxProtocol;
	}

	/**
	 * Builds a whole new Config. Usually first-run or recovery. 
	 * 
	 * @param location
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
    public static Config reset() throws URISyntaxException, IOException
    {
        Config config = new Config();
        GroupSlot system = new GroupSlot(0, "System");
        config.addGroup(system);
        for (int i = 1; i <= 16; i++)
            config.addGroup(new GroupSlot(i, "User"));
        
        File home = new File(System.getProperty("user.home"));
        home = new File(home, "media");
        config.workDirectory = home.getCanonicalPath();
        if (!home.exists())
        {
            home.mkdirs();
        }
        
        String systemfiles[] = {
                "Color Bars 1920x1080.png",
                "Grid Test 1920x1080.png",
                "Black.png",
                "White.png"
        };
        
        File sys = new File(home, "system");
        sys.mkdirs();
        
        ArrayList<File> pngFiles = new ArrayList<>();
        for (String f : systemfiles)
        {
            InputStream is = config.getClass().getClassLoader().getResourceAsStream("images/system/"+f);
            if (is != null)
            {
                byte[] data = new byte[8192];
                File file = new File(sys, f);
                pngFiles.add(file);
                FileOutputStream os = new FileOutputStream(file);
                int n = 0;
                while ((n = is.read(data)) > 0)
                {
                    os.write(data, 0, n);
                }
                is.close();
                os.close();
            }
        }
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

	public int getUniverse()
	{
		return universe;
	}

	public void setUniverse(int universe)
	{
		this.universe = universe;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public HWXY getDisplayPosition()
	{
		return displayPosition;
	}

	public void setDisplayPosition(HWXY displayPosition)
	{
		this.displayPosition = displayPosition;
	}

	public HWXY getEditorPosition()
	{
		return editorPosition;
	}

	public void setEditorPosition(HWXY editorPosition)
	{
		this.editorPosition = editorPosition;
	}

	public String getNetworkInterface()
	{
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface)
	{
		this.networkInterface = networkInterface;
	}

    public ScreenInfo getScreenInfo()
    {
        return screenInfo;
    }

    public void setScreenInfo(ScreenInfo screenInfo)
    {
        this.screenInfo = screenInfo;
    }

	public String getWorkDirectory()
	{
		return workDirectory;
	}

	public void setWorkDirectory(String workDirectory)
	{
		this.workDirectory = workDirectory;
	}

	@XmlAttribute(name="debug")
    public boolean isDebugEnabled()
    {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled)
    {
        this.debugEnabled = debugEnabled;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class HWXY
{
	double x, y, width, height;
	
	@Override
	public boolean equals(Object obj)
	{
	    HWXY z = (HWXY) obj;
        return x == z.x && y == z.y && width == z.width && height == z.height;
	}
	
	@Override
	public int hashCode()
	{
        return Double.hashCode(x) ^ Double.hashCode(y) ^ Double.hashCode(width) ^ Double.hashCode(height);
	}
}
