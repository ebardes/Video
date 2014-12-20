package org.bardes.mplayer;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class MasterLayer
{

    private Stage stage;
    private Parent root;

    public MasterLayer(Stage stage)
    {
        this.stage = stage;
        root = stage.getScene().getRoot();
    }

    public void shift(int xShift, int yShift, int xScale, int yScale)
    {
        root.setTranslateX((xShift - 32768) / 8);
        root.setTranslateY((yShift - 32768) / 8);
        
        root.setScaleX(xScale / 8192.0);
        root.setScaleY(yScale / 8192.0);
    }

}
