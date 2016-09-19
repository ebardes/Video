package org.bardes.mplayer;

import org.bardes.mplayer.personality.DMXPersonality;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

public class ComboBoxEditingCell extends TableCell<LayerConfig, DMXPersonality>
{

	private ComboBox<DMXPersonality> comboBox = new ComboBox<>();

	ComboBoxEditingCell()
	{
	}

	@Override
	public void startEdit()
	{
		if (!isEmpty())
		{
			super.startEdit();
			createComboBox();
			setText(null);
			setGraphic(comboBox);
		}
	}

	@Override
	public void cancelEdit()
	{
		super.cancelEdit();

		setText(getTyp().toString());
		setGraphic(null);
	}

	@Override
	public void updateItem(DMXPersonality item, boolean empty)
	{
		super.updateItem(item, empty);

		if (empty)
		{
			setText(null);
			setGraphic(null);
		}
		else
		{
			if (isEditing())
			{
				if (comboBox != null)
				{
					comboBox.setValue(getTyp());
				}
				setText(getTyp().toString());
				setGraphic(comboBox);
			}
			else
			{
				setText(getTyp().toString());
				setGraphic(null);
			}
		}
	}

	private void createComboBox()
	{
		comboBox = new ComboBox<>();
		comboBox.getItems().addAll(DMXPersonality.values());
		comboBoxConverter(comboBox);
		comboBox.valueProperty().set(getTyp());
		comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		comboBox.setOnAction((e) -> {
//			System.out.println("Committed: " + comboBox.getSelectionModel().getSelectedItem());
			commitEdit(comboBox.getSelectionModel().getSelectedItem());
		});
	}

	private void comboBoxConverter(ComboBox<DMXPersonality> comboBox)
	{
		// Define rendering of the list of values in ComboBox drop down.
		comboBox.setCellFactory((c) -> {
			return new ListCell<DMXPersonality>()
			{
				@Override
				protected void updateItem(DMXPersonality item, boolean empty)
				{
					super.updateItem(item, empty);

					if (item == null || empty)
					{
						setText(null);
					}
					else
					{
						setText(item.toString());
					}
				}
			};
		});
	}

	private DMXPersonality getTyp()
	{
		return getItem() == null ? DMXPersonality.LITE : getItem();
	}
}