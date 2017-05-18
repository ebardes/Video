package org.bardes.mplayer;

import org.bardes.mplayer.Slot.Type;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

@SuppressWarnings("restriction")
public class BasicLayer implements Layer
{
	private BorderPane pane;
	private Slot slot;
	private Node node;
	private Node previewNode;
	private boolean running = false;
    private int layerId;
    private int dimmer = -1;
    private int lastGroup = -1;
    private int lastSlot = -1;
    private MediaPlayer mp;
    private boolean debugging = false;
	private int playMode;
	private int volume;
	private PerspectiveTransform shapper = null;
	private ColorAdjust colorAdjust;
	private BorderPane previewPane;

	public BasicLayer(int layerId, BorderPane pane)
	{
		this.layerId = layerId;
        this.pane = pane;
        this.colorAdjust = new ColorAdjust();
	}
	
	private static double d(int n)
	{
		return (double)n / 255.0;
	}
	
	@Override
	public void setDimmer(int n)
	{
	    if (dimmer == n)
	        return;
	    
	    if (debugging)
	        System.out.println("Dimmer="+n+" " + layerId);
	    
	    this.dimmer = n;
		pane.setOpacity(d(n));
	    
		if (slot != null)
		{
		    if (slot.getType() == Type.VIDEO && node != null)
		    {
		        if (n > 0 && !running)
		        {
		            start(node, "A");
    			}
		        if (n == 0 && running)
		        {
		            stop("A");
		        }
		    }
		}
	}

	@Override
	public void setItem(int groupId, int slotId)
	{
	    if (groupId == lastGroup && slotId == lastSlot)
	        return;
	    
	    if (debugging)
	        System.out.format("setItem(%d, %d) %d%n", groupId, slotId, layerId);
	        
        if (running && dimmer > 0 && (node instanceof MediaView) || (slot != null && slot.getType() == Type.VIDEO))
        {
            stop("B");
        }
        
        lastGroup = groupId;
        lastSlot = slotId;
        
 		Config config = Main.getConfig();
		GroupSlot gs = config.getGroup(groupId);
		if (gs != null)
		{
			slot = gs.get(slotId);
			if (slot != null)
			{
			    Runnable task = new Runnable() { 
			        public void run() {
        				Node x = slot.getNode(Main.display);
        				if (x != null)
        				{
        				    Platform.runLater(new Runnable() {
        				        public void run() {
                				    node = x;
                				    x.setEffect(colorAdjust);
                					pane.setCenter(x);
                					if (previewPane != null)
                					{
                						previewNode = slot.getPreview(previewPane);
                						previewPane.setCenter(previewNode);
//                						previewPane.setPrefSize(240, 200);
//                						previewPane.setMaxSize(240, 200);
                						previewPane.layout();
                					}
                					
                    				if (slot.getType() == Type.VIDEO && dimmer > 0 && !running)
                    				{
                				        start(x, "B");
                    				}
        				        }
        				    });
        				}
        				else
        				{
        					pane.setCenter(null);
        					node = null;
        				}
    			    }
			    };
			    Main.loader.submit(task);
			}
			else
			{
				pane.setCenter(null);
				node = null;
			}
		}
	}

	/**
	 * @param mp
	 */
	protected void start(Node node, String debug)
	{
	    if (debugging)
	        System.out.println("Start "+layerId+" " + debug);
		MediaView mv = (MediaView) node;
		MediaPlayer mp = mv.getMediaPlayer();
        this.mp = mp;
        switch (playMode)
        {
        case 1:
            mp.setCycleCount(1);
            break;
            
        case 2:
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.setOnEndOfMedia(new Runnable() {
                public void run()
                {
                    mp.play();
                }
            });
            break;
            
        default:
        }
		mp.setVolume(d(volume));
        mp.play();
		running = true;
	}

	/**
	 * @param mp
	 */
	protected void stop(String debug)
	{
	    if (debugging)
	        System.out.println("Stop "+layerId+" " + debug);
		if (mp != null)
		{
		    mp.stop();
		    mp = null;
		}
		running = false;
	}

	@Override
	public void setVolume(int volume, int pan)
	{
		this.volume = volume;
		if (mp != null)
		{
			mp.setVolume(d(volume));
			mp.setBalance((pan - 128) / 128.0);
		}
	}

    protected void shift(Pane node, int xShift, int yShift, int xScale, int yScale, int rotate)
    {
    	if (node == null)
    		return;
    	
    	node.setTranslateX((double)(xShift - 32768) / 16.0);
    	node.setTranslateY((double)(yShift - 32768) / 16.0);
    	
    	node.setScaleX(xScale / 16384.0);
    	node.setScaleY(yScale / 16384.0);
    	
    	node.setRotate((double)(rotate - 32768) / 182.04); // 182.04 = 32768 / 180
    }

    @Override
	public void shift(int xShift, int yShift, int xScale, int yScale, int rotate)
	{
    	shift(pane, xShift, yShift, xScale, yScale, rotate);
    	shift(previewPane, xShift, yShift, xScale, yScale, rotate);
	}
    
    @Override
    public void setPlayMode(int playMode)
    {
        this.playMode = playMode;
    }
    
    /**
     * Nomenclature is borrowed from the GrandMA2 Blade Shapper Dialog
     * see https://help.malighting.com/view/reference/Ref_Window_PopUp_SpecializedDialog_Shaper_BladeMode.html
     * 
     * @param a1 UpperLeftX
     * @param a2 LowerLeftX
     * @param b1 UpperLeftY
     * @param b2 UpperRightY
     * @param c1 UpperRightX
     * @param c2 LowerRightX
     * @param d1 LowerRightY
     * @param d2 LowerLeftY
     */
    @Override
    public void shapper(int blade_1a, int blade_1b, int blade_2a, int blade_2b, int blade_3a, int blade_3b, int blade_4a, int blade_4b)
    {
    	if (blade_1a == 0 && blade_1b == 0 && blade_2a == 0 && blade_2b == 0 && blade_3a == 0 && blade_3b == 0 && blade_4a == 0 && blade_4b == 0)
    	{
    		if (shapper != null)
    		{
    			colorAdjust.setInput(null);
    			shapper = null;
    		}
    		return;
    	}
    	else
    	{
    		if (shapper == null)
    		{
    			shapper = new PerspectiveTransform();
    			colorAdjust.setInput(shapper);
    		}
    	}

    	double minX = 0;
    	double minY = 0;
    	double maxX = pane.getWidth();
    	double maxY = pane.getHeight();
    	
    	double halfX = (maxX - minX) / 2.0; // Half width
    	double halfY = (maxY - minY) / 2.0; // Half height
    	
    	shapper.setUlx(minX + (halfX * d(blade_1a)));
    	shapper.setLlx(minX + (halfX * d(blade_3a)));
    	shapper.setUly(minY + (halfY * d(blade_1b)));
    	shapper.setUry(minY + (halfY * d(blade_2b)));
    	shapper.setUrx(maxX - (halfX * d(blade_2a)));
    	shapper.setLrx(maxX - (halfX * d(blade_4a)));
    	shapper.setLry(maxY - (halfY * d(blade_4b)));
    	shapper.setLly(maxY - (halfY * d(blade_3b)));
    	
    	if (this.layerId == 0 && debugging) 
    	{
        	System.out.printf("UL(%f,%f)  UR(%f,%f)\n", shapper.getUlx(), shapper.getUly(), shapper.getUrx(), shapper.getUry());
        	System.out.printf("LL(%f,%f)  LR(%f,%f)\n", shapper.getLlx(), shapper.getLly(), shapper.getLrx(), shapper.getLry());
        	System.out.println();
    	}
    }
    
    @Override
    public void colorAdjust(int brightness, int contrast, int saturation, int hue)
    {
    	this.colorAdjust.setBrightness(d(brightness - 128) * 2.0);
    	this.colorAdjust.setContrast(d(contrast - 128) * 2.0);
    	this.colorAdjust.setSaturation(d(saturation - 128) * 2.0);
    	this.colorAdjust.setHue(d(hue - 128) * 2.0);
    }
    
    @Override
    public void setPreviewPane(BorderPane previewPane)
    {
		this.previewPane = previewPane;
		previewPane.setCenter(previewNode);
    }
}
