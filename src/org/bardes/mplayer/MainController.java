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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.bardes.mplayer.Slot.Type;

public class MainController implements Initializable
{
	@FXML
	Pane root;

	@FXML
	TreeView<Slot> treeView;

	@FXML
	HTMLEditor htmlEditor;

	@FXML
	TilePane tilePane;

	@FXML
	BorderPane groupPane;

	@FXML
	AnchorPane filePane;

	@FXML
	AnchorPane newItemPane;

	@FXML
	TextField fileNameField;

	@FXML
	TextField fileDescriptionField;

	@FXML
	Button fileChooseButton;

	@FXML
	ImageView imageView;

	@FXML
	ToolBar groupToolbar;

	@FXML
	TextField newSlotId;

	@FXML
	Button delItem;

	@FXML
	ToggleGroup newItemType;

	@FXML
	Label labelStatus;

	@FXML
	TextField groupDescriptionField;

	private BorderPane lastItem;

	Map<Slot.Type, Image> imageMap = new HashMap<Slot.Type, Image>();

	private Config config;

	File configLocation = new File("config.xml");

	private Slot selected;


	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			info("Initializing ...");
			URL x = new URL(location, "images");

			imageMap.put(Slot.Type.GROUP, new Image(x + "/ic_folder_black_18dp.png"));
			imageMap.put(Slot.Type.IMAGE, new Image(x + "/ic_image_black_18dp.png"));
			imageMap.put(Slot.Type.VIDEO, new Image(x + "/ic_play_circle_fill_black_18dp.png"));
			imageMap.put(Slot.Type.WEB, new Image(x + "/ic_public_black_18dp.png"));

			treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Slot>>()
			{
				@Override
				public void changed(ObservableValue<? extends TreeItem<Slot>> observable, TreeItem<Slot> oldValue, TreeItem<Slot> newValue)
				{
					if (oldValue != null)
						saveView(oldValue.getValue());
					if (newValue != null)
						updateViews(newValue.getValue());
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
			if (configLocation.exists())
			{
				config = Config.load(configLocation);
			}
			else
			{
				config = Config.load(new File("masterconfig.xml"));
				if (config == null)
					config = new Config();
				for (int i = 0; i <= 32; i++)
					config.addGroup(new GroupSlot(i, "User"));
				
				config.save(configLocation);
			}

			for (GroupSlot s : config.getGroups())
			{
				info("Loading Group " + s.id);
				
				TreeItem<Slot> item = new TreeItem<Slot>(s, new ImageView(imageMap.get(Slot.Type.GROUP)));
				s.treeitem = item;
				children.add(item);

				for (Slot i : s.slots)
				{
					i.group = s.id;
					TreeItem<Slot> subItem = new TreeItem<Slot>(i, new ImageView(imageMap.get(i.getType())));
					i.treeitem = subItem;
					item.getChildren().add(subItem);
				}
			}
			
			treeView.getSelectionModel().selectFirst();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		info("Ready");
	}

	protected void saveView(Slot value)
	{
		boolean changed = false;
		switch (value.getType())
		{
		case WEB:
			if (value.getReference() == null || !value.getReference().equals(htmlEditor.getHtmlText()))
			{
				value.setReference(htmlEditor.getHtmlText());
				changed = true;
			}
			break;

		case IMAGE:
			if (value.getReference() == null || !value.getReference().equals(fileNameField.getText()))
			{
				value.setReference(fileNameField.getText());
				changed = true;
			}
			if (!value.getDescription().equals(fileDescriptionField.getText()))
			{
				value.setDescription(fileDescriptionField.getText());
				value.treeitem.setValue(null);
				value.treeitem.setValue(value);
				changed = true;
			}

			break;
			
		case GROUP:
			if (value.getDescription() != null && !value.getDescription().equals(groupDescriptionField.getText()))
			{
				value.setDescription(groupDescriptionField.getText());
				value.treeitem.setValue(null);
				value.treeitem.setValue(value);
				changed = true;
			}
			break;

		default:
			break;
		}

		if (changed)
		{
			config.save(configLocation);
		}
	}

	protected void updateViews(Slot selected)
	{
		this.selected = selected;
		select(selected.getType());
		switch (selected.getType())
		{
		case WEB:
			htmlEditor.setHtmlText(selected.getReference());
			break;

		case GROUP:
			activateGroupView(selected);
			break;

		case IMAGE:
			activeImageView(selected);
			break;

		default:
			break;
		}
	}

	/**
	 * @param selected
	 */
	void activeImageView(Slot selected)
	{
		ImageView img = (ImageView) selected.getNode();
		fileNameField.setText(selected.getReference());
		fileDescriptionField.setText(selected.getDescription());
		if (img != null)
			imageView.setImage(img.getImage());
		else
			imageView.setImage(null);

		boolean disable = (selected.group == 0);
		fileNameField.setDisable(disable);
		fileDescriptionField.setDisable(disable);
		fileChooseButton.setDisable(disable);
	}

	/**
	 * @param selected
	 */
	void activateGroupView(Slot selected)
	{
		groupDescriptionField.setText(selected.getDescription());
		
		CornerRadii radii = new CornerRadii(5);
		final BorderStroke stroke = new BorderStroke(Color.valueOf("#808080"), BorderStrokeStyle.SOLID, radii, BorderWidths.DEFAULT);
		final BorderStroke fatStroke = new BorderStroke(Color.valueOf("#c0c000"), BorderStrokeStyle.SOLID, radii, BorderWidths.DEFAULT);
		final Border border = new Border(stroke);
		final Border fatBorder = new Border(fatStroke);

		ObservableList<Node> children = tilePane.getChildren();
		children.clear();
		final GroupSlot gs = (GroupSlot) selected;
		for (final Slot s : gs.slots)
		{
			Node e = s.getThumbNail();

			final BorderPane p = new BorderPane();
			p.setUserData(s);
			p.setMaxHeight(180);
			p.setMaxWidth(128);
			p.setMinHeight(180);
			p.setMinWidth(128);
			p.setBorder(border);

			if (e != null)
			{
				p.setCenter(e);
			}
			Label label = new Label(String.format("%03d", s.id));
			label.setAlignment(Pos.TOP_CENTER);
			label.setMinWidth(128);
			p.setTop(label);

			label = new Label(s.getDescription());
			label.setAlignment(Pos.BOTTOM_CENTER);
			label.setMinWidth(128);
			label.setMaxWidth(128);
			p.setBottom(label);
			p.setUserData(s);

			p.setOnMouseClicked(new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent mouseEvent)
				{
					if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
					{
						if (mouseEvent.getClickCount() == 2)
						{
							selectItem(s);
						}
						if (mouseEvent.getClickCount() == 1)
						{
							if (lastItem != null)
							{
								lastItem.setBorder(border);
							}
							p.setBorder(fatBorder);
							lastItem = p;
							lastItem.setUserData(s);
							if (gs.id != 0)
							{
								delItem.setDisable(false);
							}
						}
					}
				}
			});

			children.add(p);
		}

		groupToolbar.setDisable(gs.id == 0);
		delItem.setDisable(true);
		lastItem = null;
	}

	protected void selectItem(Slot s)
	{
		MultipleSelectionModel<TreeItem<Slot>> selectionModel = treeView.getSelectionModel();
		selectionModel.select(s.treeitem);
	}

	@FXML
	void chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(Main.window);
		if (selectedFile != null)
		{
			String path = selectedFile.toURI().toString();
			fileNameField.setText(path);
			Image image = new Image(path);
			imageView.setImage(image);
			imageView.setPreserveRatio(true);
		}
	}

	@FXML
	void addItem()
	{
		select(Type.NEW);
		newSlotId.clear();
		newSlotId.requestFocus();
	}

	@FXML
	public void fileSave()
	{
		saveView(selected);
	}

	/**
	 * 
	 */
	private void select(Type type)
	{
		filePane.setVisible(type == Type.IMAGE);
		htmlEditor.setVisible(type == Type.WEB);
		groupPane.setVisible(type == Type.GROUP);
		newItemPane.setVisible(type == Type.NEW);
	}

	@FXML
	public void addNewItem(ActionEvent event)
	{
		int newSlotId = Integer.parseInt(this.newSlotId.getText());
		GroupSlot currentGroup = (GroupSlot) selected;

		Slot slot;
		RadioButton toggle = (RadioButton) newItemType.getSelectedToggle();
		
		Type t = Type.valueOf(toggle.getId());
		
		switch (t)
		{
		case WEB:
			slot = new WebSlot();
			break;
			
		default:
			slot = new ImageSlot();
			break;
		}
		slot.id = newSlotId;
		slot.setDescription("");
		slot.group = currentGroup.id;
		currentGroup.slots.add(slot);

		TreeItem<Slot> treeItem = new TreeItem<Slot>(slot, new ImageView(this.imageMap.get(slot.getType())));
		slot.treeitem = treeItem;
		currentGroup.treeitem.getChildren().add(treeItem);

		MultipleSelectionModel<TreeItem<Slot>> selectionModel = treeView.getSelectionModel();
		selectionModel.select(slot.treeitem);
	}

	@FXML
	public void deleteItem()
	{
		
		Slot s = (Slot) lastItem.getUserData();
		tilePane.getChildren().remove(lastItem);
		GroupSlot gs = config.getGroup(s.group);
		gs.slots.remove(s);
		gs.treeitem.getChildren().remove(s.treeitem);
		delItem.setDisable(true);
		
		config.save(configLocation);
	}
	
	private void info(String message)
	{
		labelStatus.setText(message);
	}
}
