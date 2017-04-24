package org.bardes.mplayer.httpd;

import static spark.Spark.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.GroupSlot;
import org.bardes.mplayer.ImageSlot;
import org.bardes.mplayer.Main;
import org.bardes.mplayer.NetServer;
import org.bardes.mplayer.Slot;
import org.bardes.mplayer.VideoSlot;

import javafx.application.Platform;

/**
 * @author eric
 *
 */
public class HTTPServer implements NetServer, Runnable
{
	private MimetypesFileTypeMap typeMap;
	
	private static Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	public HTTPServer()
	{
		typeMap = new MimetypesFileTypeMap();
		typeMap.addMimeTypes("text/javascript js");
		typeMap.addMimeTypes("text/css css");
		typeMap.addMimeTypes("text/xsl xslt");
	}
	
	@Override
	public void stopServer()
	{
		stop();
	}

	@Override
	public void startServer()
	{
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public void run()
	{
		port(5678);
		staticFiles.externalLocation(Main.getConfig().getWorkDirectory());
		staticFiles.expireTime(600); 
		
		redirect.get("/", "/dynamic/index");
		get("/static/:file", (req,resp) -> {

			String file = req.params("file");
			String name = "static/" + file;
			
			URL resource = getClass().getClassLoader().getResource(name);
			if (resource == null)
			{
				resp.status(404);
				return "";
			}
			
			String type = typeMap.getContentType(file);
			resp.type(type);
			
			try (InputStream is = resource.openStream())
			{
				try (ServletOutputStream os = resp.raw().getOutputStream())
				{
					int n;
					byte buffer[] = new byte[8192];
					while ((n = is.read(buffer)) > 0)
					{
						os.write(buffer, 0, n);
					}
				}
			}
			
			return null;
		});
		
		/*
		 * Get Config Info
		 */
		get("/dynamic/:style", (req, resp) -> {
			resp.type("text/xml");
			
			StringWriter sb = new StringWriter();
			sb.append("<?xml version=\"1.0\" ?>\n");
			String style = req.params("style");
			if (style != null && style.length() > 0 && !style.equals("none"))
			{
				sb.append("<?xml-stylesheet type='text/xsl' href='/static/"+style+".xslt'  ?>\n");
			}
			
			JAXBContext c = JAXBContext.newInstance(Config.class);
			Marshaller m = c.createMarshaller();
			m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			m.marshal(Main.getConfig(), sb);
			return sb.toString();
		});
		
		post("/remove/:action/:group/:slot", (req, resp) -> {
			int group = Integer.parseInt(req.params("group"));
			int slot = Integer.parseInt(req.params("slot"));

			GroupSlot g = Main.getConfig().getGroup(group);
			if (g == null)
				halt(401, "Group not found");
			
			Slot s = g.get(slot);
			if (s == null)
				halt(401, "Slot not found");
			
			g.slots.remove(slot);
			
			try
			{
				URI url = new URI(s.getReference());
				File f = new File(url.getPath());
				f.delete();
			}
			catch (Exception ignore)
			{
			}
			
			Platform.runLater(new Runnable() {
				@Override
				public void run()
				{
					Main.controller.resetTreeView();
					Main.getConfig().save();
				}
			});
			return "";
		});
		
		/*
		 * 
		 */
		post("/upload/:action/:group/:slot", (req,resp) -> {
			try
			{
				String action = req.params("action");
				int group = Integer.parseInt(req.params("group"));
				int slot = Integer.parseInt(req.params("slot"));
				String type = req.headers("X-File-Type");
				String name = req.headers("X-File-Name");
				long lastModified = Long.parseLong(req.headers("X-File-Timestamp"));
				
				GroupSlot g = Main.getConfig().getGroup(group);
				if (g == null)
					halt(401, "Group not found");
				
				Slot s = null;
				if (slot > 0)
				{
					s = g.get(slot);
					if (action.equals("replace") && s == null)
						halt(401, "Slot not found");
				}
				
				lock.lock();
				try
				{
					for (int i = 1; i <= 255; i++)
					{
						if (g.get(i) == null)
						{
							slot = i;
							break;
						}
					}
					
					if (type.startsWith("image/"))
					{
						s = new ImageSlot();
					}
					else if (type.startsWith("video/"))
					{
						s = new VideoSlot();
					}
					else
					{
						halt(500, "Unknown file type");
					}
					if (s != null)
					{
						s.setSlot(slot);
						s.setContentType(type);
						s.setGroup(group);
						s.setDescription(name);
						
						try (ServletInputStream is = req.raw().getInputStream())
						{
							File saveTo = Main.normalizeName(s, name);
							try (FileOutputStream out = new FileOutputStream(saveTo)) 
							{
								byte[] buffer = new byte[8192];
								int n;
								
								while ((n = is.read(buffer)) > 0)
								{
									out.write(buffer, 0, n);
								}
							}
							
							g.addItem(s);
							
							s.setReference(saveTo.toURI().toString());
							s.setTimestamp(lastModified);
							s.setLength(saveTo.length());
						}
					}
				}
				finally
				{
					lock.unlock();
				}
				
				Platform.runLater(new Runnable() {
					@Override
					public void run()
					{
						lock.lock();
						try
						{
							Main.controller.resetTreeView();
							Main.getConfig().save();
						}
						finally
						{
							lock.unlock();
						}
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		    return "";
		});
	}
}
