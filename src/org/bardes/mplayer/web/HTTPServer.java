package org.bardes.mplayer.web;

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

	public void run()
	{
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new HttpServerInitializer());

			Channel ch = b.bind(PORT).sync().channel();

			ch.closeFuture().sync();
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
	 */
	public static void startServer()
	{
		Thread t = new Thread(new HTTPServer());
		t.setName("Web Server");
		t.setDaemon(true);
		t.start();
	}
}
