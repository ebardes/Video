package org.bardes.mplayer;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
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

import org.bardes.mplayer.Slot.Type;
import org.bardes.mplayer.cue.Cue;
import org.bardes.mplayer.cue.CueLayerInfo;
import org.bardes.mplayer.cue.CueStack;
import org.bardes.mplayer.net.DMXProtocol;
import org.bardes.mplayer.net.Interface;
import org.bardes.mplayer.personality.DMXPersonality;

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
	Pane videoContainer;

	@FXML
	TabPane tabBar;

	@FXML
	TextField configUniverse;

	@FXML
	TextField configOffset;

	@FXML
	TilePane previewPane;

	@FXML
	ChoiceBox<DMXPersonality> dmxPersonality;

	@FXML
	CheckBox imgAspectRatio;

	@FXML
	Button fileUpdateButton;

	@FXML
	ChoiceBox<DMXProtocol> dmxSource;

	@FXML
	ListView<Cue> cueList;

	@FXML
	ChoiceBox<Interface> nwInterface;
	
	@FXML
	TextField cueTime;

	private BorderPane lastItem;
	
	Map<Slot.Type, Image> imageMap = new HashMap<Slot.Type, Image>();

	private Slot selected;

	private Config config;

	private enum TabID
	{
		CONTENT,
		CONFIG,
		PREVIEW,
		CUE,
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
//					if (oldValue != null)
//						deactivateView(oldValue.getValue());
					if (newValue != null && newValue.equals(observable.getValue()))
						activateView(newValue.getValue());
				}
			});
			
			addNumberCheck(this.configOffset);
			addNumberCheck(this.configUniverse);
			addNumberCheck(this.cueTime);
			
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
			
			/*
			 * Populate and select the DMX Source box
			 */
			ObservableList<DMXProtocol> dmxProtocolItems = dmxSource.getItems();
			for (DMXProtocol p : DMXProtocol.values())
			{
				dmxProtocolItems.add(p);
			}
			DMXProtocol dmxProtocol = config.getDmxProtocol();
			if (dmxProtocol != null)
			{
				dmxSource.getSelectionModel().select(dmxProtocol);
			}
			
			/*
			 * Populate and select the DMX Personality box
			 */
			ObservableList<DMXPersonality> dmxPersonalityItems = dmxPersonality.getItems();
			for (DMXPersonality p : DMXPersonality.values())
			{
				dmxPersonalityItems.add(p);
			}
			DMXPersonality personality = config.getDmxPersonality();
			if (personality != null)
			{
				dmxPersonality.getSelectionModel().select(personality);
			}
			
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

	private ArrayList<CueLayerInfo> cueLayerInfo = new ArrayList<>();
	void initializeCueList()
	{
		for (int i = 1; i <= 4; i++)
		{
			Node node = editorPane.lookup("#cueLayer" + i);
			if (node == null)
				continue;
			AnchorPane ap = (AnchorPane) node;
			
			final CueLayerInfo cli = new CueLayerInfo();
			cli.ap = ap;
			Label title = (Label) node.lookup("#cueLayer");
			title.setText(String.format("Layer %d", i));
			
			cli.dimmerLabel = (Label) node.lookup("#cueDimmerDisplay");
			cli.volumeLabel = (Label) node.lookup("#cueVolumeDisplay");
			cli.dimmer = (Slider) node.lookup("#cueDimmer");
			cli.volume = (Slider) node.lookup("#cueVolume");
			
			cli.dimmer.valueProperty().addListener(new ChangeListener<Number>()	{
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					cli.setDimmerLevel(newValue);
				}
			});
			cli.volume.valueProperty().addListener(new ChangeListener<Number>()	{
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					cli.setVolumeLevel(newValue);
				}
			});
			
			cueLayerInfo.ensureCapacity(i);
			cueLayerInfo.add(i-1, cli);
		}
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
			
		case CUE:
			showCueTab();
			break;
		}
	}

	private void showCueTab()
	{
		ObservableList<Cue> x = this.cueList.getItems();
		CueStack stack = Main.stack;
		x.setAll(stack.getCueList());
	}

	protected void deactivateView(Slot value)
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
			changed = deactivateImage(value);
			break;
			
		case VIDEO:
			changed = deactivateVideo(value);
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

	/**
	 * @param value
	 * @param changed
	 * @return
	 */
	protected boolean deactivateVideo(Slot value)
	{
		boolean changed = false;
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
		
		return changed;
	}

	/**
	 * @param value
	 * @param changed
	 * @return
	 */
	protected boolean deactivateImage(Slot value)
	{
		boolean changed = false;
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
		
		if (value.isPerserveAspectRatio() != imgAspectRatio.isSelected())
		{
			value.setPerserveAspectRatio(imgAspectRatio.isSelected());
			changed = true;
		}
		return changed;
	}

	protected void activateView(Slot selected)
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
		ImageView img = (ImageView) selected.getPreview(imageView.getParent());
		fileNameField.setText(selected.getReference());
		fileDescriptionField.setText(selected.getDescription());
		imgAspectRatio.setSelected(selected.isPerserveAspectRatio());
		if (img != null)
		{
			Image image = img.getImage();
			imageView.setImage(image);
			imageView.setSmooth(true);
			String format = String.format("Dimensions: %dx%d" , (int) image.getWidth(), (int) image.getHeight());
			fileDetails.setText(format);
		}
		else
		{
			imageView.setImage(null);
		}

		boolean disable = (selected.group == 0);
		fileNameField.setDisable(disable);
		fileDescriptionField.setDisable(disable);
		fileChooseButton.setDisable(disable);
		fileUpdateButton.setDisable(disable);
		imgAspectRatio.setDisable(disable);
	}

	/**
	 * 
	 * @param selected
	 */
	void activeVideoView(Slot selected)
	{
		MediaView vid = (MediaView) selected.getPreview(videoContainer);
		videoNameField.setText(selected.getReference());
		videoDescriptionField.setText(selected.getDescription());
		ObservableList<Node> children = videoContainer.getChildren();
		children.clear();
		if (vid != null)
		{
		    children.add(vid);
		    vid.setFitHeight(600);
		    vid.setFitWidth(800);
		    
//			fileDetails.setText(String.format("Dimensions: %dx%d" , (int) image.getWidth(), (int) image.getHeight()));
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
		deactivateView(selected);
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
    			MediaView img = new MediaView(mp);
    			
    			ObservableList<Node> children = videoContainer.getChildren();
    			children.clear();
    			children.add(img);
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
		deactivateView(selected);
	}

	@FXML
	public void videoPlay()
	{
	    if (this.selected.getType() == Type.VIDEO)
	    {
	        VideoSlot vs = (VideoSlot) selected;
	        MediaView mv = (MediaView) vs.getPreview(null);
	        MediaPlayer mp = mv.getMediaPlayer();
	        mp.play();
	    }
	}

	@FXML
	public void videoPause()
	{
        if (this.selected.getType() == Type.VIDEO)
        {
            VideoSlot vs = (VideoSlot) selected;
            MediaView mv = (MediaView) vs.getPreview(null);
            MediaPlayer mp = mv.getMediaPlayer();
            mp.pause();
        }
	}

	@FXML
	public void videoStop()
	{
        if (this.selected.getType() == Type.VIDEO)
        {
            VideoSlot vs = (VideoSlot) selected;
            MediaView mv = (MediaView) vs.getPreview(null);
            MediaPlayer mp = mv.getMediaPlayer();
            mp.stop();
        }
	}

	@FXML
	public void saveConfig()
	{
		try	{ config.setUniverse(Integer.valueOf(configUniverse.getText())); } catch(Exception ignore) {}
		try	{ config.setOffset(Integer.valueOf(configOffset.getText()));     } catch(Exception ignore) {}
		
		config.setNetworkInterface(nwInterface.getValue().getName());
		
		DMXPersonality selectedPersonality = DMXPersonality.LITE;
		DMXProtocol selectedProtocol = DMXProtocol.SACN;
		try
		{
			selectedPersonality = dmxPersonality.getSelectionModel().getSelectedItem();
			config.setDmxPersonality(selectedPersonality);
		} catch (Exception ignore) {}
		
		try
		{
			selectedProtocol = dmxSource.getSelectionModel().getSelectedItem();
			config.setDmxProtocol(selectedProtocol);
		} catch (Exception ignore) {}
		
		Main.restartListener(selectedProtocol, selectedPersonality);
		config.save();
		cancelConfig(); // Using the side effect of ensuring the form matches the config.
	}

	@FXML
	public void cancelConfig()
	{
		dmxSource.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DMXProtocol>()
		{
			@Override
			public void changed(ObservableValue<? extends DMXProtocol> observable, DMXProtocol oldValue, DMXProtocol newValue)
			{
				dmxProtocolChange();
			}
		});
		
		configUniverse.setText(String.valueOf(config.getUniverse()));
		configOffset.setText(String.valueOf(config.getOffset()));
		
		try
		{
			Interface selected = null;
			ObservableList<Interface> items = nwInterface.getItems();
			items.clear();
			
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface nwIf = interfaces.nextElement();
				if (nwIf.supportsMulticast())
				{
					Interface i = new Interface();
					i.network = nwIf;
					items.add(i);
					
					if (i.getName().equals(config.getNetworkInterface()))
					{
						selected = i;
					}
				}
			}
			
			if (selected != null)
				nwInterface.getSelectionModel().select(selected);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		dmxProtocolChange();
	}
	
	void dmxProtocolChange()
	{
		DMXProtocol dmxProtocol = dmxSource.getValue();
		
		boolean disableNetwork = dmxProtocol == DMXProtocol.INTERNAL;
		
		nwInterface.setDisable(disableNetwork);
		configOffset.setDisable(disableNetwork);
		configUniverse.setDisable(disableNetwork);
		dmxPersonality.setDisable(disableNetwork);
	}

	public void addNumberCheck(final TextField text)
	{
		text.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if (newValue.equals("0"))
				{
					text.selectEnd();
					return;
				}
				if (newValue.equals(""))
				{
					text.setText("0");
					return;
				}
				if (!newValue.matches("\\d+(\\.\\d*)?"))
				{
					text.setText(oldValue);
					return;
				}
				try
				{
					int fraction = (newValue.indexOf('.') >= 0) ? 1 : 0; 
					NumberFormat nf = DecimalFormat.getNumberInstance();
					nf.setMinimumFractionDigits(fraction);
					text.setText(nf.format(Double.valueOf(newValue)));
				}
				catch (Exception ignore) {}
			}
		});
	}
}
