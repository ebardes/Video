package org.bardes.mplayer;

public interface Layer
{
	void setDimmer(int intensity);
	void setItem(int group, int slot);
	void setVolume(int volume);
    void shift(int xShift, int yShift, int xScale, int yScale, int rotate);
    void setPlayMode(int playMode);
	void shapper(int a1, int a2, int b1, int b2, int c1, int c2, int d1, int d2);
}
