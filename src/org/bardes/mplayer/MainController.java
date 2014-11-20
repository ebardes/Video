package org.bardes.mplayer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController
{
	@FXML
	TableView<Cue> cuelist;

	@FXML
	private TableColumn description;

	@FXML
	private TableColumn cue;

	@FXML
	private TableColumn time;

	public void addCue()
	{
		System.out.println("AddCue()");

		Cue c = new Cue();

		c.cue.set(1.0);

		ObservableList<Cue> items = cuelist.getItems();
		items.add(c);
	}
}
