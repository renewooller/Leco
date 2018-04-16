/*
 * Created on 24/01/2005
 *
 * @author Rene Wooller
 */
package com.lemu.music;

/**
 * @author wooller
 *
 *24/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class CueEvent {

	private MusicGenerator musicGenerator;
	
	/**
	 * 
	 */
	public CueEvent() {
		super();
	}
	
	public MusicGenerator getMusicGenerator() {
		return musicGenerator;
	}
	public void setMusicGenerator(MusicGenerator musicGenerator) {
		this.musicGenerator = musicGenerator;
	}
}
