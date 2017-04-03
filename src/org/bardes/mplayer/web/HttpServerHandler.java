/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.bardes.mplayer.web;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.bardes.mplayer.Config;
import org.bardes.mplayer.Main;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

/**
 * @author eric
 *
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter
{
	private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

	private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

	private static final int HTTP_CACHE_SECONDS = 60;

	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");

	private static final AsciiString CONNECTION = new AsciiString("Connection");

	private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

	private MimetypesFileTypeMap mimeTypesMap;

	/**
	 * 
	 */
	public HttpServerHandler()
	{
		mimeTypesMap = new MimetypesFileTypeMap();
		mimeTypesMap.addMimeTypes("image/png png");
		mimeTypesMap.addMimeTypes("text/css css");
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		if (msg instanceof HttpRequest)
		{
			HttpRequest req = (HttpRequest) msg;
			String uri = req.uri();

			if (uri.equals("/") || uri.equals(""))
			{
				sendRedirect(ctx, "/dynamic/index");
				return;
			}
			if (HttpUtil.is100ContinueExpected(req))
			{
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}

			String fileName = null;
			try
			{
				fileName = URLDecoder.decode(uri.substring(1), "UTF-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				e1.printStackTrace();
			}
			if (uri.startsWith("/dynamic/"))
			{
				try
				{
					boolean keepAlive = HttpUtil.isKeepAlive(req);
					FullHttpResponse response = xslt(uri.substring(9));

					if (!keepAlive)
					{
						ctx.write(response).addListener(ChannelFutureListener.CLOSE);
					}
					else
					{
						response.headers().set(CONNECTION, KEEP_ALIVE);
						ctx.write(response);
					}
				}
				catch (Exception e)
				{
					byte[] content = e.getMessage().getBytes();
					FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(content));
					response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
					ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				}
			}
			else if (uri.startsWith("/static"))
			{
				InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
				if (is != null)
				{
					try
					{
						byte buffer[] = new byte[8192];
						int n;
						ByteArrayOutputStream s = new ByteArrayOutputStream();
						while ((n = is.read(buffer)) > 0)
						{
							s.write(buffer, 0, n);
						}
						is.close();

						boolean keepAlive = HttpUtil.isKeepAlive(req);
						FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(s.toByteArray()));
						response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
						setContentTypeHeader(response, fileName);

						if (!keepAlive)
						{
							ctx.write(response).addListener(ChannelFutureListener.CLOSE);
						}
						else
						{
							response.headers().set(CONNECTION, KEEP_ALIVE);
							ctx.write(response);
						}
						return;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
				sendError(ctx, NOT_FOUND);
			}
			else
			{
				File previewFile = new File(Main.getConfig().getWorkDirectory(), fileName);
				try
				{
					sendFile(ctx, req, previewFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * 
	 * @param queryString
	 * @return A response object
	 * @throws JAXBException
	 */
	private FullHttpResponse xslt(String queryString) throws JAXBException 
	{
		JAXBContext c = JAXBContext.newInstance(Config.class);
		Marshaller m = c.createMarshaller();

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		out.println("<?xml version=\"1.0\" ?>");
		if (!queryString.equals("none"))
		{
			out.print("<?xml-stylesheet type=\"text/xsl\" href=\"/static/"+queryString+".xslt\" ?>");
		}
		
		m.marshal(Main.getConfig(), out);
		byte[] content = baos.toByteArray();
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
		response.headers().set(CONTENT_TYPE,  "text/xml");
		response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
		
		return response;

	}

	private void sendRedirect(ChannelHandlerContext ctx, String newUri)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
		response.headers().set(HttpHeaderNames.LOCATION, newUri);

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void sendFile(ChannelHandlerContext ctx, HttpRequest request, File file) throws IOException
	{
		if (file.isHidden() || !file.exists())
		{
			sendError(ctx, NOT_FOUND);
			return;
		}

		if (!file.isFile())
		{
			sendError(ctx, FORBIDDEN);
			return;
		}

		// Cache Validation
		String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
		if (ifModifiedSince != null && !ifModifiedSince.isEmpty())
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
			try
			{
				Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

				// Only compare up to the second because the datetime format we
				// send to the client does not have milliseconds
				long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
				long fileLastModifiedSeconds = file.lastModified() / 1000;
				if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds)
				{
					sendNotModified(ctx);
					return;
				}
			}
			catch (ParseException ignore) // If the date is mangled, then just
											// send the file again.
			{
			}
		}

		RandomAccessFile raf;
		try
		{
			raf = new RandomAccessFile(file, "r");
		}
		catch (FileNotFoundException ignore)
		{
			sendError(ctx, NOT_FOUND);
			return;
		}
		long fileLength = raf.length();

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		HttpUtil.setContentLength(response, fileLength);
		setContentTypeHeader(response, file);
		setDateAndCacheHeaders(response, file);
		if (HttpUtil.isKeepAlive(request))
		{
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}

		// Write the initial line and the header.
		ctx.write(response);

		// Write the content.
		ChannelFuture sendFileFuture;
		ChannelFuture lastContentFuture;
		if (ctx.pipeline().get(SslHandler.class) == null)
		{
			sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
			// Write the end marker.
			lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		}
		else
		{
			sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());
			// HttpChunkedInput will write the end marker (LastHttpContent) for
			// us.
			lastContentFuture = sendFileFuture;
		}

		/*
		sendFileFuture.addListener(new ChannelProgressiveFutureListener()
		{
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total)
			{
				if (total < 0)
				{ // total unknown
					System.err.println(future.channel() + " Transfer progress: " + progress);
				}
				else
				{
					System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
				}
			}

			@Override
			public void operationComplete(ChannelProgressiveFuture future)
			{
				System.err.println(future.channel() + " Transfer complete.");
			}
		});
		*/

		// Decide whether to close the connection or not.
		if (!HttpUtil.isKeepAlive(request))
		{
			// Close the connection when the whole content is written out.
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * 
	 * @param ctx
	 *            Context
	 * @param status
	 *            Error Code
	 */
	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * When file timestamp is the same as what the browser is sending up, send a
	 * "304 Not Modified"
	 *
	 * @param ctx
	 *            Context
	 */
	private void sendNotModified(ChannelHandlerContext ctx)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
		setDateHeader(response);

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Sets the Date header for the HTTP response
	 *
	 * @param response
	 *            HTTP response
	 */
	private void setDateHeader(FullHttpResponse response)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

		Calendar time = new GregorianCalendar();
		response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
	}

	/**
	 * Sets the Date and Cache headers for the HTTP Response
	 *
	 * @param response
	 *            HTTP response
	 * @param fileToCache
	 *            file to extract content type
	 */
	private void setDateAndCacheHeaders(HttpResponse response, File fileToCache)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

		// Date header
		Calendar time = new GregorianCalendar();
		response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

		// Add cache headers
		time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
		response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
		response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
		response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
	}

	/**
	 * Sets the content type header for the HTTP Response
	 *
	 * @param response
	 *            HTTP response
	 * @param file
	 *            file to extract content type
	 */
	private void setContentTypeHeader(HttpResponse response, File file)
	{
		setContentTypeHeader(response, file.getPath());
	}
	
	/**
	 * Sets the content type header for the HTTP Response
	 *
	 * @param response
	 *            HTTP response
	 * @param file
	 *            file to extract content type
	 */
	private void setContentTypeHeader(HttpResponse response, String name)
	{
		String contentType = mimeTypesMap.getContentType(name);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
	}
}