package org.bardes.mplayer.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;
import org.w3c.dom.Document;

import fi.iki.elonen.NanoHTTPD;


public class HTTPServer extends NanoHTTPD
{
	private static HTTPServer me;

	public HTTPServer() throws IOException
	{
		super(8080);
		start();
	}
	
	protected byte[] process(String queryString) throws TransformerException, JAXBException, ParserConfigurationException
	{
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(queryString + ".xslt");
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer(new StreamSource(inputStream));
		
		JAXBContext c = JAXBContext.newInstance(Config.class);
		Marshaller m = c.createMarshaller();
		
		Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
//		dom.createProcessingInstruction(target, data)
		
		m.marshal(Main.getConfig(), dom);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		t.transform(new DOMSource(dom), new StreamResult(out));
		return out.toByteArray();
	}

	@Override
	public Response serve(IHTTPSession session)
	{
		String uri = session.getUri();
		if (uri.equals("") || uri.equals("/"))
		{
			Response response = newFixedLengthResponse(Response.Status.TEMPORARY_REDIRECT, "", "Redirecting");
			response.addHeader("Location", "/dynamic/index");
			return response;
		}
		if (uri.startsWith("/dynamic"))
		{
			try
			{
				byte[] buf;
				uri = uri.substring(9);
				buf = process(uri);
				return newFixedLengthResponse(Response.Status.OK, "text/html", new ByteArrayInputStream(buf), buf.length);
			}
			catch (TransformerException | JAXBException | ParserConfigurationException e)
			{
				e.printStackTrace();
			}
		}
		String fileName = uri.substring(1);
		try
		{
			fileName = URLDecoder.decode(fileName, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}
		if (uri.startsWith("/static"))
		{
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
			if (is != null)
			{
				try
				{
					return newChunkedResponse(Response.Status.PARTIAL_CONTENT, "", is);
				}
				finally
				{
				}
			}
		}
		File previewFile = new File(Main.getConfig().getWorkDirectory(), fileName);
		if (previewFile.exists())
		{
			FileInputStream is;
			try
			{
				is = new FileInputStream(previewFile);
				return newChunkedResponse(Response.Status.PARTIAL_CONTENT, "", is);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return super.serve(session);
	}
	
	public static void startServer()
	{
		try
		{
			me = new HTTPServer();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
