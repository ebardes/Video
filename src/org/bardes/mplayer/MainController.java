package org.bardes.mplayer;

import java.io.File;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

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

	@FXML
	Label fileDetails;
	
	@FXML
	BorderPane editorPane;

	@FXML
	AnchorPane videoPane;

	@FXML
	TextField videoNameField;

	@FXML
	Button videoChooseButton;

	@FXML
	TextField videoDescriptionField;

	@FXML
	Label videoDetails;

	@FXML
	MediaView videoView;

	@FXML
	TabPane tabBar;

	@FXML
	TextField configUniverse;

	@FXML
	TextField configOffset;

	@FXML
	TilePane previewPane;

	private BorderPane lastItem;

	Map<Slot.Type, Image> imageMap = new HashMap<Slot.Type, Image>();

	private Slot selected;

	private Config config;

	private enum TabID
	{
		CONTENT,
		CONFIG,
		PREVIEW,
	}

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
			
			tabBar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
			{
				@Override
				public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
				{
					changeTab(newValue);
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
			config = Main.getConfig();
			
			for (GroupSlot s : config.getGroups())
			{
				info("Loading Group " + s.id);
				
				TreeItem<Slot> item = new TreeItem<Slot>(s, new ImageView(imageMap.get(Slot.Type.GROUP)));
				s.treeitem = item;
				children.add(item);

				for (Slot i : s.slots.values())
				{
					i.group = s.id;
					TreeItem<Slot> subItem = new TreeItem<Slot>(i, new ImageView(imageMap.get(i.getType())));
					i.treeitem = subItem;
					item.getChildren().add(subItem);
				}
			}
			
			GroupSlot system = config.getGroup(0);
			MultipleSelectionModel<TreeItem<Slot>> selectionModel = treeView.getSelectionModel();
			selectionModel.select(system.treeitem);
			treeView.requestFocus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		info("Ready");
	}

	protected void changeTab(Tab tab)
	{
		TabID tabId = TabID.valueOf(tab.getId());
		switch (tabId)
		{
		case CONFIG:
			cancelConfig(); // quick and dirty way to repopulate the page
			break;
			
		case CONTENT:
			// do nothing
			break;
			
		case PREVIEW:
			// do nothing
			break;
		}
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
				
				activeImageView(value);
			}

			break;
			
		case VIDEO:
			if (value.getReference() == null || !value.getReference().equals(videoNameField.getText()))
			{
				value.setReference(videoNameField.getText());
				changed = true;
			}
			if (!value.getDescription().equals(videoDescriptionField.getText()))
			{
				value.setDescription(videoDescriptionField.getText());
				value.treeitem.setValue(null);
				value.treeitem.setValue(value);
				changed = true;
				
				activeVideoView(value);
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
			config.save();
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
			
		case VIDEO:
			activeVideoView(selected);
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
		ImageView img = (ImageView) selected.getNode(null);
		fileNameField.setText(selected.getReference());
		fileDescriptionField.setText(selected.getDescription());
		if (img != null)
		{
			Image image = img.getImage();
			imageView.setImage(image);
			
			fileDetails.setText(String.format("Dimensions: %dx%d" , (int) image.getWidth(), (int) image.getHeight()));
		}
		else
		{
			imageView.setImage(null);
		}

		boolean disable = (selected.group == 0);
		fileNameField.setDisable(disable);
		fileDescriptionField.setDisable(disable);
		fileChooseButton.setDisable(disable);
	}

	/**
	 * 
	 * @param selected
	 */
	void activeVideoView(Slot selected)
	{
		MediaView vid = (MediaView) selected.getNode(null);
		videoNameField.setText(selected.getReference());
		videoDescriptionField.setText(selected.getDescription());
		if (vid != null)
		{
			MediaPlayer mp = vid.getMediaPlayer();
			if (mp != null)
			{
    			mp.play();
    			mp.pause();
    			Duration seekTime = new Duration(5000);
    			mp.seek(seekTime);
    			videoView.setMediaPlayer(mp);
			}
//			fileDetails.setText(String.format("Dimensions: %dx%d" , (int) image.getWidth(), (int) image.getHeight()));
		}
		else
		{
			videoView.setMediaPlayer(null);
		}
		
		boolean disable = (selected.group == 0);
		videoNameField.setDisable(disable);
		videoDescriptionField.setDisable(disable);
		videoChooseButton.setDisable(disable);
	}
	
	/**
	 * @param selected
	 */
	void activateGroupView(Slot selected)
	{
		groupDescriptionField.setText(selected.getDescription());
		
		CornerRadii radii = new CornerRadii(5);
		final BorderStroke stroke = new BorderStroke(Color.valueOf("#808080"), BorderStrokeStyle.SOLID, radii, BorderWidths.DEFAULT);
		final BorderStroke fatStroke = new BorderStroke(Color.valueOf("#c00000"), BorderStrokeStyle.SOLID, radii, BorderWidths.DEFAULT);
		final Border border = new Border(stroke);
		final Border fatBorder = new Border(fatStroke);

		ObservableList<Node> children = tilePane.getChildren();
		children.clear();
		final GroupSlot gs = (GroupSlot) selected;
		for (final Slot s : gs.slots.values())
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
		videoPane.setVisible(type == Type.VIDEO);
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
		
		Type t = Type.valueOf(Type.class, toggle.getId());
		
		switch (t)
		{
		case WEB:
			slot = new WebSlot();
			break;
			
		case VIDEO:
			slot = new VideoSlot();
			break;
			
		default:
			slot = new ImageSlot();
			break;
		}
		slot.id = newSlotId;
		slot.setDescription("");
		slot.group = currentGroup.id;
		currentGroup.slots.put(slot.id, slot);

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
		
		config.save();
		
		treeView.getSelectionModel().select(gs.treeitem);
	}
	
	private void info(String message)
	{
		labelStatus.setText(message);
	}

	@FXML
	public void chooseVideo()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Video Files", "*.mp4", "*.m4v"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Sound Files", "*.mp4", "*.wav"));
		File selectedFile = fileChooser.showOpenDialog(Main.window);
		if (selectedFile != null)
		{
			String path = selectedFile.toURI().toString();
			try
			{
    			Media media = new Media(path);
    			videoNameField.setText(path);
    			MediaPlayer mp = new MediaPlayer(media);
    			videoView.setMediaPlayer(mp);
    			videoDetails.setText("");
			}
			catch (Exception e)
			{
			    videoDetails.setText(e.getLocalizedMessage());
			}
		}
	}

	@FXML
	public void videoSave()
	{
		saveView(selected);
	}

	@FXML
	public void videoPlay()
	{
		MediaPlayer mp = videoView.getMediaPlayer();
		mp.play();
	}

	@FXML
	public void videoPause()
	{
		MediaPlayer mp = videoView.getMediaPlayer();
		mp.pause();
	}

	@FXML
	public void videoStop()
	{
		MediaPlayer mp = videoView.getMediaPlayer();
		mp.stop();
	}

	@FXML
	public void saveConfig()
	{
		try	{ config.setUniverse(Integer.valueOf(configUniverse.getText())); } catch(Exception ignore) {}
		try	{ config.setOffset(Integer.valueOf(configOffset.getText()));     } catch(Exception ignore) {}
		config.save();
		cancelConfig();
	}

	@FXML
	public void cancelConfig()
	{
		configUniverse.setText(String.valueOf(config.getUniverse()));
		configOffset.setText(String.valueOf(config.getOffset()));
	}

	@FXML
	public void validateNumber(ActionEvent event)
	{
		Object source = event.getSource();
		System.out.println("Validate Number: " + source);
		event.consume();
	}
}
