package org.bardes.mplayer;

import java.io.File;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XML
{
	public static Show loadShow(String path)
	{
		return JAXB.unmarshal(new File(path), Show.class);
	}

	public static void saveShow(Show show, String path)
	{
		JAXB.marshal(show, new File(path));
	}

	public static Cue loadCue(File f)
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(MediaCue.class);
			Unmarshaller u = jc.createUnmarshaller();
			return (Cue) u.unmarshal(f);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void saveCue(Cue cue, File baseDir)
	{
		File xml = new File(baseDir, "cue"+cue.getCue()+".xml");
		try
		{
			JAXBContext jc = JAXBContext.newInstance(MediaCue.class);
			Marshaller m = jc.createMarshaller();
			m.marshal(cue, xml);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
}
