package org.bardes.mplayer;

import java.io.File;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.bardes.mplayer.LayerManager.LayerInfo;
import org.bardes.mplayer.cue.Cue;
import org.bardes.mplayer.cue.CueLayerInfo;
import org.bardes.mplayer.cue.CueStack;
import org.bardes.mplayer.net.DMXProtocol;
import org.bardes.mplayer.net.Interface;
import org.bardes.mplayer.personality.DMXPersonality;
import org.bardes.mplayer.ui.Dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class MainController implements Initializable
{
	public static final String MOVIE_THUMB = "-thumbnail.png";

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
	Button delLayerButton;

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
	Button videoUpdate;

	@FXML
	TabPane tabBar;

	@FXML
	TextField configUniverse;

	@FXML
	TableView<LayerConfig> fixtureTable;
	
	@FXML
	TableColumn<LayerConfig, Integer> addressColumn;
	
	@FXML
	TableColumn<LayerConfig, Integer> footprintColumn;
	
	@FXML
	TableColumn<LayerConfig, DMXPersonality> personalityColumn;
	
	@FXML
	TilePane previewPane;

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
	
	@FXML
	Tab cueTab;
	
	@FXML
	TextField workDirectory;

	private ArrayList<CueLayerInfo> cueLayerInfo = new ArrayList<>();

	private BorderPane lastItem;
	
	Map<Type, Image> imageMap = new HashMap<Type, Image>();

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

			imageMap.put(Type.GROUP, new Image(x + "/ic_folder_black_18dp.png"));
			imageMap.put(Type.IMAGE, new Image(x + "/ic_image_black_18dp.png"));
			imageMap.put(Type.VIDEO, new Image(x + "/ic_play_circle_fill_black_18dp.png"));
//			imageMap.put(Slot.Type.WEB, new Image(x + "/ic_public_black_18dp.png"));
			
			treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null && newValue.equals(observable.getValue()))
					activateView(newValue.getValue());
			});
			
			addNumberCheck(this.configUniverse);
			addNumberCheck(this.cueTime);

			tabBar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				changeTab(newValue);
			});
			
			dmxSource.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				dmxProtocolChange();
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
			 * Populate Personality control
			 */
			if (config.getLayers().size() == 0)
			{
				addLayer();
			}
			else
			{
				fixtureTable.getItems().addAll(config.getLayers());
			}
			fixtureTable.setEditable(true);
			
			Callback<TableColumn<LayerConfig, Integer>, TableCell<LayerConfig, Integer>> addressCellFactory = (TableColumn<LayerConfig, Integer> param) -> new AddressEditingCell();
			addressColumn.setCellFactory(addressCellFactory);
			addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddressProperty());
			addressColumn.setOnEditCommit(
	                (TableColumn.CellEditEvent<LayerConfig, Integer> t) -> {
	                    ((LayerConfig) t.getTableView().getItems()
	                    .get(t.getTablePosition().getRow()))
	                    .setAddress(t.getNewValue());
	                });
			
			Callback<TableColumn<LayerConfig, DMXPersonality>, TableCell<LayerConfig, DMXPersonality>> personalityCellFactory = (TableColumn<LayerConfig, DMXPersonality> param) -> new ComboBoxEditingCell();
			personalityColumn.setCellFactory(personalityCellFactory);
			personalityColumn.setCellValueFactory(cellData -> cellData.getValue().getPersonalityProperty());
			personalityColumn.setOnEditCommit(
					(TableColumn.CellEditEvent<LayerConfig, DMXPersonality> t) -> {
						int row = t.getTablePosition().getRow();
						((LayerConfig) t.getTableView().getItems()
			            .get(row))
						.setPersonality(t.getNewValue());
						
						LayerConfig layerConfig = config.getLayers().get(row);
						layerConfig.setPersonality(t.getNewValue());
					});
			
			fixtureTable.selectionModelProperty().get().select(0);

			/*
			 * 
			 */
			resetTreeView();
			
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
	
	/**
	 * 
	 */
	public synchronized void resetTreeView()
	{
		if (treeView == null)
			return;
			
		TreeItem<Slot> root = treeView.getRoot();
		ObservableList<TreeItem<Slot>> children = root.getChildren();
		children.clear();
		
		for (GroupSlot s : config.getGroups())
		{
			info("Loading Group " + s.id);
			
			TreeItem<Slot> item = new TreeItem<Slot>(s, new ImageView(imageMap.get(Type.GROUP)));
			s.treeitem = item;
			children.add(item);

			Iterator<Slot> it = s.slots.values().iterator();
			while (it.hasNext())
			{
				Slot i = it.next();
				if (i.getReference() == null)
				{
					it.remove();
					continue;
				}
				URI uri = URI.create(i.getReference());
				if (!new File(uri.getPath()).exists())
				{
					it.remove();
					continue;
				}
				i.group = s.id;
				TreeItem<Slot> subItem = new TreeItem<Slot>(i, new ImageView(imageMap.get(i.getType())));
				i.treeitem = subItem;
				item.getChildren().add(subItem);
			}
		}
	}

	private void resetPreviewPane()
	{
		ObservableList<Node> children = previewPane.getChildren();
		previewPane.getStyleClass().add("previewcontainer");
		previewPane.getStylesheets().add("style.css");
		
		children.clear();
		for (LayerInfo layer : Main.layoutManager.layers)
		{
			BorderPane p = new BorderPane();
			p.setPrefSize(240, 200);
			p.setMaxSize(240, 200);
			p.getStyleClass().add("preview");
			p.getStylesheets().add("style.css");
			
			Text label = new Text(
					String.format("DMX: %03d\n%s", layer.start, layer.personality.toString())
					);
			p.setBottom(label);
			
			BorderPane bp = new BorderPane();
	        bp.maxHeightProperty().bind(p.heightProperty());
	        bp.maxWidthProperty().bind(p.widthProperty());
			p.getChildren().add(bp);
			children.add(p);
			
			BorderPane.setAlignment(bp, Pos.CENTER);
			layer.setPreviewPane(bp);
		}
	}

	@SuppressWarnings("unchecked")
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
			
			cli.group = (ChoiceBox<GroupSlot>) node.lookup("#cueGroup");
			cli.slot = (ChoiceBox<Slot>) node.lookup("#cueSlot");
			
			cli.cueView = (Pane) node.lookup("#cueView");
			
			Set<GroupSlot> groups = config.getGroups();
			cli.group.getItems().setAll(groups);
			
			cli.group.valueProperty().addListener((observable, oldValue, newValue) -> {
				cli.changeGroup(newValue);
            });
			
			cli.slot.valueProperty().addListener((observable, oldValue, newValue) -> {
				cli.changeSlot(newValue);
			});
			
			cli.dimmer.valueProperty().addListener((observable, oldValue, newValue) -> {
				cli.setDimmerLevel(newValue);
			});
			cli.volume.valueProperty().addListener((observable, oldValue, newValue) -> {
				cli.setVolumeLevel(newValue);
			});
			
			cueLayerInfo.ensureCapacity(i);
			cueLayerInfo.add(i-1, cli);
		}
		
		MultipleSelectionModel<Cue> selectionModel = cueList.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        	changeCue(newValue);
        });
        
        if (selectionModel.getSelectedIndex() < 0)
            selectionModel.selectFirst();
	}

	protected void changeCue(Cue cue)
    {
	    if (cue != null)
	        System.out.format("Cue %s go%n", cue.getId());
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
			Platform.runLater(new Runnable(){
                public void run() {
                    treeView.requestFocus();
                }});
			break;
			
		case PREVIEW:
			resetPreviewPane();
			break;
			
		case CUE:
			showCueTab();
			break;
		}
	}

	private void showCueTab()
	{
		ObservableList<Cue> x = cueList.getItems();
		CueStack stack = Main.stack;
		x.setAll(stack.getCueList());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cueList.requestFocus();
            }
        });
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
				s.makeThumbNail(p);
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

			p.setOnMouseClicked((mouseEvent) -> {
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
		fileChooser.setTitle("Open Image File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(Main.window);
		if (selectedFile != null)
		{
			MultipleSelectionModel<TreeItem<Slot>> selectionModel = treeView.getSelectionModel();
			TreeItem<Slot> selectedItem = selectionModel.getSelectedItem();
			Slot slot = selectedItem.getValue();
			
			String path = Main.normalize(selectedFile, slot);
			fileNameField.setText(path);
			fileNameField.setEditable(false);
			fileDescriptionField.setText(selectedFile.getName());
			Image image = new Image(path);
			imageView.setImage(image);
			imageView.setPreserveRatio(true);
			fileSave();
			fileUpdateButton.requestFocus();
		}
	}

	@FXML
	void addItem()
	{
		int n = 1;
		if (selected != null && selected.getType() == Type.GROUP) {
			GroupSlot gs = ((GroupSlot) selected);
			while (gs.slots.containsKey(n)) {
				n++;
			}
		}
		select(Type.NEW);
		newSlotId.setText(Integer.toString(n));
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
		gs.slots.remove(s.id);
		gs.treeitem.getChildren().remove(s.treeitem);
		delItem.setDisable(true);
		
		config.save();
		
		URI uri = URI.create(s.getReference());
		File ref = new File(uri.getPath());
		if (ref.exists())
		{
			ref.delete();
		}
		
		ref = new File(uri.getPath() + MOVIE_THUMB);
		if (ref.exists())
		{
			ref.delete();
		}
		
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
			MultipleSelectionModel<TreeItem<Slot>> selectionModel = treeView.getSelectionModel();
			TreeItem<Slot> selectedItem = selectionModel.getSelectedItem();
			Slot slot = selectedItem.getValue();
			
			String path = Main.normalize(selectedFile, slot);
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
    			videoDescriptionField.setText(selectedFile.getName());
    			videoUpdate.requestFocus();
    			videoSave();
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
		
		if (nwInterface != null && nwInterface.getValue() != null)
		{
			config.setNetworkInterface(nwInterface.getValue().getName());
		}
		DMXProtocol selectedProtocol = DMXProtocol.SACN;
		
		try
		{
			selectedProtocol = dmxSource.getSelectionModel().getSelectedItem();
			config.setDmxProtocol(selectedProtocol);
		} catch (Exception ignore) {}
		
		config.save();
		cancelConfig(); // Using the side effect of ensuring the form matches the config.
		
		List<LayerConfig> layers = config.getLayers();
		layers.clear();
		layers.addAll(fixtureTable.getItems());
		Main.restartListener(selectedProtocol, layers);
	}

	@FXML
	public void cancelConfig()
	{
		configUniverse.setText(String.valueOf(config.getUniverse()));
		
		try
		{
			if (config.getWorkDirectory() != null)
			{
				workDirectory.setText(config.getWorkDirectory());
			}
			
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
		configUniverse.setDisable(disableNetwork);
	}

	public static void addNumberCheck(final TextField text)
	{
		text.textProperty().addListener((observable, oldValue, newValue) -> {
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
		});
	}

	@FXML
	public void storeCue(ActionEvent event)
	{
		Object source = event.getSource();
		source.hashCode();
		
		String cueName = "";
		Cue cue = Main.stack.getCurrent();
		if (cue != null)
		{
			cueName = cue.getId().toPlainString();
		}
		BigDecimal response = Dialog.create()
		        .owner(Main.window)
		        .title("Store Cue")
		        .message("Please Enter Cue Number")
		        .showNumberInput(cueName);
		response.hashCode();
	}
	
	@FXML
	public void addLayer()
	{
		LayerConfig e = new LayerConfig();
		e.setAddress(0);
		e.setPersonality(DMXPersonality.REGULAR);
		config.getLayers().add(e);
		fixtureTable.getItems().add(e);
		
		int n = fixtureTable.getItems().size() - 1;
		fixtureTable.selectionModelProperty().get().select(n);
	}
	
	@FXML
	public void delLayer()
	{
		TableViewSelectionModel<LayerConfig> tableViewSelectionModel = fixtureTable.selectionModelProperty().get();
		int i = tableViewSelectionModel.getSelectedIndex();
		
		config.getLayers().remove(i);
		fixtureTable.getItems().remove(i);
		
		if (i >= fixtureTable.getItems().size())
			i--;
		
		tableViewSelectionModel.select(i);
		
		delLayerButton.disableProperty().setValue(fixtureTable.getItems().size() == 0);
	}

    @FXML
    public void keyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.F7)
        {
            showmode();
        }
    }

    public void showmode()
    {
        Stage d = Main.display;
        
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        d.setWidth(primaryScreenBounds.getWidth());
        d.setHeight(primaryScreenBounds.getHeight());
        
        d.setFullScreen(false);
        d.setFullScreen(true);
        
    }

	@FXML
	public void chooseWorkDir(ActionEvent event)
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File dir = dirChooser.showDialog(null);
		
		if (dir != null)
		{
			String absolutePath = dir.getAbsolutePath();
			workDirectory.setText(absolutePath);
			config.setWorkDirectory(absolutePath);
		}
	}
	
	public void Shutdown()
	{
		Platform.exit();
	}
}
