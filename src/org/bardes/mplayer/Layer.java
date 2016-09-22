package org.bardes.mplayer;

public interface Layer
{
	/**
	 * Sets the transparency of the layer (0-255).
	 * Transition from 0 to non-zero starts video content.
	 * Transition from non-zero to 0 stops video content.
	 * @param intensity
	 */
	void setDimmer(int intensity);
	
	/**
	 * Selects the group and slot of the content.
	 * If content changes, then old and new content is stopped and started if the intensity is non-zero.
	 * 
	 * @param group
	 * @param slot
	 */
	void setItem(int group, int slot);
	
	/**
	 * Sets the volume of the content.
	 *
	 * @param volume (0-255)
	 * @param pan 
	 */
	void setVolume(int volume, int pan);
	
	/**
	 * Transform the content.
	 * @param xShift (-32768 - 32767, 0 is center)
	 * @param yShift (-32768 - 32767, 0 is center)
	 * @param xScale (-32768 - 32767, 0 is neutral)
	 * @param yScale (-32768 - 32767, 0 is neutral)
	 * @param rotate (-32768 - 32767, range is translated from -180˚ to 180˚)
	 */
    void shift(int xShift, int yShift, int xScale, int yScale, int rotate);
    
    /**
     * Whether video content should loop
     * 
     * @param playMode (1 = one shot, 2 = multiple)
     */
    void setPlayMode(int playMode);
    
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
	void shapper(int a1, int a2, int b1, int b2, int c1, int c2, int d1, int d2);

	/**
	 * 
	 * @param brightness (-128, 127)
	 * @param contrast (-128, 127)
	 * @param saturation (-128, 127)
	 * @param hue (-128, 127)
	 */
	void colorAdjust(int brightness, int contrast, int saturation, int hue);
}
