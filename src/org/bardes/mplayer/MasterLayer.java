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

    public void shift(int xShift, int yShift, int xScale, int yScale, int rotate)
    {
        root.setTranslateX((double)(xShift - 32768) / 8.0);
        root.setTranslateY((double)(yShift - 32768) / 8.0);
        
        root.setScaleX(xScale / 8192.0);
        root.setScaleY(yScale / 8192.0);
        
        root.setRotate((double)(rotate - 32768) / 182.04); // 182.04 = 32768 / 180
    }

}
