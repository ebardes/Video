package org.bardes.mplayer.web;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author eric
 *
 */
public class HTTPServer implements Runnable 
{

	private static final int PORT = 8080;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public void run()
	{
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new HttpServerInitializer());

			Channel channel = b.bind(PORT).sync().channel();
			channel.closeFuture().sync();
			
			System.out.println("Closing");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * Launch the Web Server
	 * @return 
	 */
	public static HTTPServer startServer()
	{
		HTTPServer server = new HTTPServer();
		Thread t = new Thread(server);
		t.setName("Web Server");
		t.setDaemon(true);
		t.start();
		return server;
	}
	
	/**
	 * 
	 */
	public void stopServer()
	{
		workerGroup.shutdownGracefully(1, 1, TimeUnit.SECONDS);
		bossGroup.shutdownGracefully(1, 1, TimeUnit.SECONDS);
	}
}
