package org.bardes.mplayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class MainController implements Initializable
{
	@FXML
	TreeView<Slot> treeView;
	
	Map<Slot.Type, Image> imageMap = new HashMap<Slot.Type, Image>();
	
	Slot selected;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			URL x = new URL(location, "images");
			
			imageMap.put(Slot.Type.GROUP,  new Image(x + "/ic_folder_black_18dp.png"));
			imageMap.put(Slot.Type.IMAGE, new Image(x + "/ic_image_black_18dp.png"));
			imageMap.put(Slot.Type.VIDEO, new Image(x + "/ic_play_circle_fill_black_18dp.png"));
			
			treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Slot>>() {
				@Override
				public void changed(ObservableValue<? extends TreeItem<Slot>> observable, TreeItem<Slot> oldValue, TreeItem<Slot> newValue)
				{
					if (newValue != null)
						selected = newValue.getValue();
					System.out.println(selected);
				}
			});
			
			TreeItem<Slot> root = treeView.getRoot();
			if (root == null)
			{
				root = new TreeItem<Slot>();
				root.setExpanded(true);
				treeView.setRoot(root);
			}
						
			ObservableList<TreeItem<Slot>> children = root.getChildren();
			File f = new File("config.xml");
			Config config;
			if (f.exists())
			{
				config = Config.load(f);
			}
			else
			{
				config = new Config();
				GroupSlot system = new GroupSlot(0, "System");
				config.addGroup(system);
				config.addGroup(new GroupSlot(1, "User"));
				config.addGroup(new GroupSlot(2, "User"));
				
				system.addItem(new ImageSlot(1, "Black", "black.png"));
				system.addItem(new WebSlot(2, "White", "<html><body></body></html>"));
			}
			
			for (GroupSlot s : config.getGroups())
			{
				TreeItem<Slot> item = new TreeItem<Slot>(s, new ImageView(imageMap.get(Slot.Type.GROUP)));
				children.add(item);
				
				for (Slot i : s.slots)
				{
					item.getChildren().add(new TreeItem<Slot>(i, new ImageView(imageMap.get(i.getType()))));
				}
			}
			
			config.save(f);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}
