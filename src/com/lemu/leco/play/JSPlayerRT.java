package com.lemu.leco.play;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import javax.swing.SwingUtilities;

import jm.midi.MidiSynth;
import jm.music.data.Score;
import ren.gui.ParameterMap;
import ren.gui.seqEdit.BaseBeatTracker;
import ren.gui.seqEdit.BeatListener;
import ren.gui.seqEdit.LPlayerBeatTracker;
import ren.music.Player.Playable;
import ren.music.Player.PrePostPlayerEventListener;

/**
 * Class for LeCo playback using JavaSound
 * 
 * @author Rene Wooller
 *
 */
public class JSPlayerRT implements Playable, UserModifiedBeatListener {

	private MidiSynth midiSynthOne, midiSynthTwo;// = new MidiSynth();
	
	private Score score = new Score();
	
	private ArrayList<PrePostPlayerEventListener> prepoplavlis = new ArrayList<PrePostPlayerEventListener>(10);

	private BaseBeatTracker beatTracker;
	
	private double masterTempo = 100;
	
	public JSPlayerRT() {
		super();
		this.midiSynthOne = new MidiSynth();
		beatTracker = new LPlayerBeatTracker();
		beatTracker.construct(new ParameterMap().construct(32, 16777216, 512, "scope"));
		beatTracker.addUserModBeatListener(this);
	}
	
	public JSPlayerRT(Score s) {
		this();
		this.setScore(s);  
	}

	public void addPrePostPlayerEventListener(
			PrePostPlayerEventListener pregoeli) {
		// TODO Auto-generated method stub
		
	}

	public BaseBeatTracker getBeatTracker() {
		// TODO Auto-generated method stub
		return null;
	}

	public void go() {
		// TODO Auto-generated method stub
		
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void setBeatTracker(BaseBeatTracker bt) {
		// TODO Auto-generated method stub
		
	}

	public void setScore(Score s) {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void beatFired(double newBeat) {
		// TODO Auto-generated method stub
		
	}
	
	
}
