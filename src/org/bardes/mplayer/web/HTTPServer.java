package org.bardes.mplayer.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.w3c.dom.Document;

public class HTTPServer
{
	private static HTTPServer server;

	public HTTPServer()
	{
		HttpServer server;
		try
		{
			String workDirectory = Main.getConfig().getWorkDirectory();
			server = HttpServer.createSimpleServer(workDirectory);
			
			ServerConfiguration config = server.getServerConfiguration();
			config.setSendFileEnabled(true);
			config.setMaxFormPostSize(-1);

			config.addHttpHandler(new HttpHandler()
			{
				@Override
				public void service(Request request, Response response) throws Exception
				{
					String queryString = request.getRequestURI();
					queryString = queryString.replaceAll("/dynamic/", "");
					
					process(queryString, response);
				}
			}, "/dynamic");
			
			config.addHttpHandler(new HttpHandler()
			{
				
				@Override
				public void service(Request request, Response response) throws Exception
				{
					String requestURI = request.getRequestURI();
					requestURI = requestURI.substring(1);
					InputStream is = getClass().getClassLoader().getResourceAsStream(requestURI);
					if (is == null)
					{
						response.sendError(404);
						return;
					}
					OutputStream os = response.getOutputStream();
					
					int n;
					byte buffer[] = new byte[8192];
					while ((n = is.read(buffer)) > 0)
					{
						os.write(buffer, 0, n);
					}
					
					is.close();
					os.close();
				}
			}, "/static");
			
			
			server.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected void process(String queryString, Response response) throws TransformerException, JAXBException, ParserConfigurationException
	{
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(queryString + ".xslt");
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer(new StreamSource(inputStream));
		
		JAXBContext c = JAXBContext.newInstance(Config.class);
		Marshaller m = c.createMarshaller();
		
		Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
//		dom.createProcessingInstruction(target, data)
		
		m.marshal(Main.getConfig(), dom);
		t.transform(new DOMSource(dom), new StreamResult(response.getOutputStream()));
	}

	public static void start()
	{
		server = new HTTPServer();
	}
}
