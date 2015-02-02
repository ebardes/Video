package org.bardes.mplayer;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class ScreenInfo
{
    @XmlAttribute
    private int screenId;
    
    @XmlTransient
    private Screen screen;
    
    private HWXY hwxy;
    
    public ScreenInfo()
    {
    }
    
    public ScreenInfo(Screen screen)
    {
        this.setScreen(screen);
    }

    @Override
    public String toString()
    {
        String extra = "";
        if (Screen.getPrimary().equals(getScreen()))
        {
            extra = " Primary";
        }
        Rectangle2D bounds = getScreen().getBounds();
        return "(" + (int)bounds.getWidth() + " x " + (int) bounds.getHeight() + ")" + extra;
    }

    public Screen getScreen()
    {
        return screen;
    }

    public void setScreen(Screen screen)
    {
        this.screen = screen;
        Rectangle2D b = screen.getBounds();
        if (b != null)
        {
            hwxy = new HWXY();
            hwxy.height = b.getHeight();
            hwxy.width = b.getWidth();
            hwxy.x = b.getMinX();
            hwxy.y = b.getMinY();
        }
    }

    public HWXY getHwxy()
    {
        return hwxy;
    }

    public void setHwxy(HWXY hwxy)
    {
        this.hwxy = hwxy;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        
        ScreenInfo si = (ScreenInfo) obj;
        return si.hwxy == hwxy;
    }
    
    @Override
    public int hashCode()
    {
        return hwxy.hashCode();
    }
}
