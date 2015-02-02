package org.bardes.mplayer;

public interface Layer
{
	void setDimmer(int intensity);
	void setItem(int group, int slot);
	void setVolume(int volume);
    void shift(int xShift, int yShift, int xScale, int yScale, int rotate);
    void setPlayMode(int playMode);
}
