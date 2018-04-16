package com.lemu.gui.musicArea;
//import lplay.LPlayer;
import com.lemu.music.MusicGenerator;

import jmms.TickListener;

/**
 
 */

public interface MGCInterface extends TickListener {

	public MusicGenerator getMusicGenerator();
	// so that morph can grab the music generators that are in this
	 
	
}