package org.bardes.mplayer;

import org.bardes.mplayer.Slot.Type;

import javafx.scene.Node;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class BasicLayer implements Layer
{
	private BorderPane pane;
	private Slot slot;
	private Node node;
	private boolean running = false;
    private int layerId;
    private int dimmer = -1;
    private int lastGroup = -1;
    private int lastSlot = -1;
    private MediaPlayer mp;
    private Stage stage;
    private boolean debugging;
	private int playMode;
	private int volume;
	private PerspectiveTransform shapper = null;

	public BasicLayer(int layerId, Stage stage, BorderPane pane)
	{
		this.layerId = layerId;
        this.stage = stage;
        this.pane = pane;
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
	    
	    lastGroup = groupId;
	    lastSlot = slotId;
	    
	    if (debugging)
	        System.out.format("setItem(%d, %d) %d%n", groupId, slotId, layerId);
	        
        if (running && dimmer > 0 && node instanceof MediaView)
        {
            stop("B");
        }
        
		Config config = Main.getConfig();
		GroupSlot gs = config.getGroup(groupId);
		if (gs != null)
		{
			slot = gs.get(slotId);
			if (slot != null)
			{
				Node x = slot.getNode(stage);
				if (x != null)
				{
				    node = x;
				    pane.setCenter(x);
//				    if (shapper != null)
//				        pane.setEffect(shapper);
    				
    				if (slot.getType() == Type.VIDEO && dimmer > 0 && !running)
    				{
				        start(x, "B");
    				}
				}
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
	public void setVolume(int volume)
	{
		this.volume = volume;
		if (mp != null)
		{
			mp.setVolume(d(volume));
		}
	}

    @Override
    public void shift(int xShift, int yShift, int xScale, int yScale, int rotate)
    {
        BorderPane root = pane;
        root.setTranslateX((double)(xShift - 32768) / 64.0);
        root.setTranslateY((double)(yShift - 32768) / 64.0);
        
        root.setScaleX(xScale / 32768.0);
        root.setScaleY(yScale / 32768.0);
        
        root.setRotate((double)(rotate - 32768) / 182.04); // 182.04 = 32768 / 180
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
        if (shapper == null)
        {
            shapper = new PerspectiveTransform();
        }
        
    	double minX = 0;
    	double minY = 0;
    	double maxX = pane.getWidth();
    	double maxY = pane.getHeight();
    	
    	double halfX = (maxX - minX) / 2.0; // Half width
    	double halfY = (maxY - minY) / 2.0; // Half height
    	
//    	shapper.setUlx(minX + 10);
//    	shapper.setLlx(minX + 10);
//    	shapper.setUly(minY + 10);
//    	shapper.setUry(minY + 10);
//    	shapper.setUrx(maxX - 10);
//    	shapper.setLrx(maxX - 10);
//    	shapper.setLry(maxY - 10);
//    	shapper.setLly(maxY - 10);
    	shapper.setUlx(minX + (halfX * d(blade_4b)));
    	shapper.setLlx(minX + (halfX * d(blade_4a)));
    	shapper.setUly(minY + (halfY * d(blade_1a)));
    	shapper.setUry(minY + (halfY * d(blade_1b)));
    	shapper.setUrx(maxX - (halfX * d(blade_2a)));
    	shapper.setLrx(maxX - (halfX * d(blade_2b)));
    	shapper.setLry(maxY - (halfY * d(blade_3a)));
    	shapper.setLly(maxY - (halfY * d(blade_3b)));
    	
//    	if (this.layerId == 0) 
//    	{
//        	System.out.printf("UL(%f,%f)  UR(%f,%f)\n", shapper.getUlx(), shapper.getUly(), shapper.getUrx(), shapper.getUry());
//        	System.out.printf("LL(%f,%f)  LR(%f,%f)\n", shapper.getLlx(), shapper.getLly(), shapper.getLrx(), shapper.getLry());
//        	System.out.println();
//    	}
    }
}
