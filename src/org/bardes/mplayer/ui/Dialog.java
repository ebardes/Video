package org.bardes.mplayer.ui;

import java.math.BigDecimal;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.bardes.mplayer.MainController;

public class Dialog
{
//	private Stage owner;
	private String title;
	private String message;
	
	static private class Value
	{
		Object value;
	}

	private Dialog()
	{
	}
	
	public static Dialog create()
	{
		return new Dialog();
	}

	public Dialog owner(Stage owner)
	{
//		this.owner = owner;
		return this;
	}

	public Dialog title(String title)
	{
		this.title = title;
		return this;
	}

	public Dialog message(String message)
	{
		this.message = message;
		return this;
	}

	public BigDecimal showNumberInput(String string)
	{
		Stage dialog = new Stage();
		Pane root = new Pane();
		Scene scene = new Scene(root);
	
		Label message = new Label(this.message);
		TextField tf = new TextField(string);
		tf.setPrefWidth(200);
		MainController.addNumberCheck(tf);
		
		Button okButton = new Button("Okay");
		okButton.setDefaultButton(true);
		Button cancelButton = new Button("Cancel");
		cancelButton.setCancelButton(true);
		
		FlowPane fp =  new FlowPane(20, 20, okButton, cancelButton);
		fp.setAlignment(Pos.CENTER);
		
		VBox vb = new VBox(5, message, tf, fp);
		vb.setAlignment(Pos.CENTER);
		scene.setRoot(vb);

		Value x = new Value();
		okButton.setOnAction((event) -> {
			BigDecimal value = new BigDecimal(tf.getText());
			x.value = value;
			dialog.close();
		});
		cancelButton.setOnAction((event) -> {
			dialog.close();
		});
		
		dialog.setTitle(title);
		dialog.setScene(scene);
		dialog.centerOnScreen();
		dialog.showAndWait();
		
		return (BigDecimal) x.value;
	}
	
}
