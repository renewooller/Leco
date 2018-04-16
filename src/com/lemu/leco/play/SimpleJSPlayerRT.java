package com.lemu.leco.play;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * This class will simply use a callback to  play a JS seq that can be modified in realtime
 * 
 * Rene Wooller
 */
public class SimpleJSPlayerRT {
	
	protected Sequencer sequencer;
	
	protected Sequence seq;
	
	private double tempo = Double.valueOf(System.getProperty("com.lemu.default_tempo", "120"));
	
	private double tickCount = 0;
	
	private double resolution = Double.valueOf(System.getProperty("com.lemu.default_beat_resolution", "0.25"));
		
	public SimpleJSPlayerRT() {
		
	}
	
}
