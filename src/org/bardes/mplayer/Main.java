package org.bardes.mplayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.bardes.mplayer.net.NetworkListener;
import org.bardes.mplayer.personality.LitePersonality;
import org.bardes.mplayer.personality.Personality;
import org.bardes.mplayer.sacn.E131Listener;

/**
 *
 * @author dean
 */
public class Main extends Application
{

	public static Window window;

	public static void main(String[] args)
	{
		launch(args);
	}

	private Stage display;
	
	private List<Layer> layers = new ArrayList<>();
	
	private static NetworkListener listener;
	
	private static Config config;
	
	private static File configLocation = new File("config.xml");

	public static Config getConfig()
	{
		return config;
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			ClassLoader cl = getClass().getClassLoader();
			URL url = cl.getResource("main.fxml");
			
			if (configLocation.exists())
			{
				config = Config.load(configLocation);
			}
			else
			{
				config = Config.reset(url);
				config.save();
			}

			restartListener();
			
			StackPane displayPane = new StackPane();
			displayPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			Scene displayScene = new Scene(displayPane);
			display = new Stage();
			display.setScene(displayScene);
			display.setTitle("Display");
			
			HWXY pos = config.getDisplayPosition();
			display.setHeight(pos.height);
			display.setWidth(pos.width);
			display.setX(pos.x);
			display.setY(pos.y);
			display.show();
			
			for (int i = 0; i < 4; i++)
			{
				Pane e = new Pane();
				e.setOpacity(0.0);
				displayPane.getChildren().add(e);
				Layer l = new BasicLayer(e);
				layers.add(l);
			}
			
			window = primaryStage;
			pos = config.getEditorPosition();
			primaryStage.setX(pos.x);
			primaryStage.setY(pos.y);
			primaryStage.setWidth(pos.width);
			primaryStage.setHeight(pos.height);
			
			Parent p = FXMLLoader.load(url);
			Scene myScene = new Scene(p);
			primaryStage.setTitle("Astraeus Media System");
			primaryStage.setScene(myScene);
			
			primaryStage.show();
			
			Personality personality = new LitePersonality(layers); 
			listener.setPersonality(personality);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception
	{
		HWXY pos = new HWXY();
		pos.height = display.getHeight();
		pos.width = display.getWidth();
		pos.x = display.getX();
		pos.y = display.getY();
		config.setDisplayPosition(pos);
		
		pos = new HWXY();
		pos.height = window.getHeight();
		pos.width = window.getWidth();
		pos.x = window.getX();
		pos.y = window.getY();
		config.setEditorPosition(pos);
		
		config.save();
		
		super.stop();
	}

	/**
	 * 
	 */
	public void restartListener()
	{
		if (listener != null)
			listener.stop();
		
		listener = new E131Listener();
		listener.start();
	}
}