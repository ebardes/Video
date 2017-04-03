package org.bardes.mplayer.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NETTYSystem extends ChannelInboundHandlerAdapter
{
	private int port = 7373;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public void process() throws InterruptedException
	{
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() 
				{
						@Override
						public void initChannel(SocketChannel ch) throws Exception
						{
							ch.pipeline().addLast(new NETTYSystem());
						}
				})
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync(); // (7)

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		}
		finally
		{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		((ByteBuf) msg).release();
	}

	public void start()
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					process();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
		t.setDaemon(true);
		t.start();
		
		t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				
			}
		});
	}
	
	public void stop()
	{
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}
