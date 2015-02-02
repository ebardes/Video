package org.bardes.mplayer;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MasterLayer
{
    private StackPane root;

    public MasterLayer(Stage stage)
    {
        root = (StackPane) stage.getScene().getRoot();
    }

    public void shift(int xShift, int yShift, int xScale, int yScale, int rotate)
    {
        root.setTranslateX((double)(xShift - 32768) / 8.0);
        root.setTranslateY((double)(yShift - 32768) / 8.0);
        
        root.setScaleX(xScale / 8192.0);
        root.setScaleY(yScale / 8192.0);
        
        root.setRotate((double)(rotate - 32768) / 182.04); // 182.04 = 32768 / 180
    }

    public void color(int red, int green, int blue)
    {
        Paint color = new Color(red / 255.0, green / 255.0, blue / 255.0, 1.0);
        root.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
}
