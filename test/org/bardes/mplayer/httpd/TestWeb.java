package org.bardes.mplayer.httpd;

import org.bardes.mplayer.Main;

public class TestWeb
{
	public static void main(String[] args) throws Exception
	{
		Main main = new Main();
		main.initConfig();
		
		HTTPServer httpServer = new HTTPServer();
		httpServer.run();
	}
}
