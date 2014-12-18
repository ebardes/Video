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
	
	private List<Pane> layers = new ArrayList<>();
	
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

			StackPane displayPane = new StackPane();
			displayPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			Scene displayScene = new Scene(displayPane);
			display = new Stage();
			display.setScene(displayScene);
			display.setTitle("Display");
			display.show();
			
			for (int i = 0; i < 4; i++)
			{
				Pane e = new Pane();
				displayPane.getChildren().add(e);
				layers.add(e);
			}
			
			window = primaryStage;
			Parent p = FXMLLoader.load(url);
			Scene myScene = new Scene(p);
			primaryStage.setTitle("Astraeus Media System");
			primaryStage.setScene(myScene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}