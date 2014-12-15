package org.bardes.mplayer;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dean
 */
public class Main extends Application
{

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		ClassLoader cl = getClass().getClassLoader();
		URL url = cl.getResource("main.fxml");
		try
		{
			Parent p = FXMLLoader.load(url);
			Scene myScene = new Scene(p);
			primaryStage.setScene(myScene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}