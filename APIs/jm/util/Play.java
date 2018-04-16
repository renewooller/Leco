/*
 
 <This Java Class is part of the jMusic API version 1.5, March 2004.>
 
 Copyright (C) 2000 Andrew Sorensen & Andrew Brown
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or any
 later version.
 
 This program is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */ 

package jm.util;

import jm.midi.MidiSynth;
import jm.music.data.*;
import jm.JMC;

/*  Enhanced by the Derryn McMaster 2003 */

public class Play implements JMC{
    /**
    * The current (only) one time playback thread.
     */
    private static PlayThread pt;
    /**
    * The current (only) repeated playback thread.
     */
    private static PlayCycle pc;
    /**
     * True if a midiCycle is currently playing
     */
    private static boolean cyclePlaying = false;
    /**
     * True if a one time MIDI playback is currently playing
     */
    private static boolean midiPlaying = false;
    /** A thread started to time the duration of playbnack */
    private static Thread pauseThread;
	
    /**
     * Constructor
     */
    public Play() {}
	
    /**
     * Used by infinite cycle players (eg midiCycle())
     * to stop the playback loop.
     */
    public static void stopCycle(){
	cyclePlaying = false;
        if (pc != null) pc.stopPlayCycle();
    }
	
	/**
     * Used by infinite cycle player threads to check
     * cyclePlaying status.
     */
	public static boolean cycleIsPlaying(){
		return cyclePlaying;
	}
	
    /**
     * Thread.sleeps for a period of 1 score play length
     * (i.e. the time it would take for the specified 
     * score to play).
     * Can be used in conjunction with midiCycle() if the 
     * score requires a re-compute just before being
     * replayed.  (i.e. sleeps for one loop)  Should 
     * be placed immediately after the Play.midiCycle() 
     * command to ensure that the time waited is for 
     * the currently playing score, and not subject 
     * to any changes made to the score since.
     * @param Score The score used for timing the sleep.
     */
	public static void waitCycle(Score s){
		try{	// wait duration plus 2 second for construction and reverb trail
			Thread.sleep((int)(1000.0 * 60.0 / s.getTempo() * s.getEndTime() + 2000));
		}catch(Exception e){e.printStackTrace();}
	}
	/**
     * Playback a MIDI file from disk.
     * @param fileName The name of the file to play back.
     */
    public static void mid(String fileName) {
        Score score = new Score();
        Read.midi(score, fileName);
        Play.midi(score);
    }
	
	//----------------------------------------------
	// MidiSynth - JavaSound MIDI playback
	//----------------------------------------------
	/**
     * Playback the jMusic score JavaSound MIDI
     * @param Note The note to be played
     */
	public static void midi(Note n) {
        midi(n, true);
	}	
    
	/**
     * Playback the jMusic score JavaSound MIDI
     * @param Phrase The Phrase to be played
     */
	public static void midi(Phrase phr) {
        midi(phr, true);
	}
    
	/**
     * Playback the jMusic score JavaSound MIDI
     * @param Part The Part to be played
     */
	public static void midi(Part p) {
        midi(p, true);
	}
	
	
	/**
     * Playback the jMusic score JavaSound MIDI using the default value of 
     * true for 'exit' - See Play.midi(Score,boolean)
     * @param Score: The score to be played.
     */ 
	public static void midi(Score s) {
		midi(s,true);
	}
	
	
    /**
     * Playback the jMusic score JavaSound MIDI
     * @param Note The note to be played
     * @param exit Crash program after playabck? true or false
     */
	public static void midi(Note n, boolean exit) {
		Score s = new Score("One note score", 60);
        s.addPart(new Part(new Phrase(n)));
        midi(s,exit);
	}	
    
	/**
     * Playback the jMusic score JavaSound MIDI
     * @param Phrase The Phrase to be played
     * @param exit Crash program after playabck? true or false
     */
	public static void midi(Phrase phr, boolean exit) {
		double tempo = 60;
		if(phr.getTempo() != Phrase.DEFAULT_TEMPO) tempo = phr.getTempo();
		Score s = new Score(phr.getTitle() + " score", tempo);
        if (phr.getTempo() != Phrase.DEFAULT_TEMPO) s.setTempo(phr.getTempo());
        s.addPart(new Part(phr));
        midi(s,exit);
	}
    
	/**
     * Playback the jMusic score JavaSound MIDI
     * @param Part The Part to be played
     * @param exit Crash program after playabck? true or false
     */
	public static void midi(Part p, boolean exit) {
		double tempo = 60;
		if(p.getTempo() != Part.DEFAULT_TEMPO) tempo = p.getTempo();
		Score s = new Score(p.getTitle() + " score", tempo);
        if (p.getTempo() != Part.DEFAULT_TEMPO) s.setTempo(p.getTempo());
        s.addPart(p);
        midi(s,exit);
	}
	
    /**
     * Playback the jMusic score JavaSound MIDI.
     * This method exits the application on completion.
     * To avoid this exit call, pass false as the second argument.
     * @param Score: The score to be played.
     * @param boolean exit: If true, System.exit(0) will be called at the end.
     */ 
	public static void midi(Score s, boolean exit) {
        if(midiPlaying) stopMidi();
        midiPlaying = true;
        Score defensiveCopy = s.copy();
        
        System.out.print("-- Constructing MIDI file from'" + s.getTitle() + "'...");
        pt = new PlayThread(defensiveCopy);
        new Thread(pt).start();
        System.out.print(" Playing with JavaSound ...");
        midiWait(s, exit);
        /*
         try {
             waitCycle(defensiveCopy);
             if (exit)System.exit(0); // horrid but less confusing for beginners
         }catch (Exception e) {
             System.err.println("jMusic MIDI Playback Error:" + e);
             return;
         }
         System.out.println(" Completed --");
         */
	}
    
    private static void midiWait(final Score score, final boolean exit) {
        pauseThread = new Thread( new Runnable() {
            public void run() {
                try {
                    pauseThread.sleep((int)(score.getEndTime() * 60.0 /score.getTempo() * 1000.0) + 3000);
                } catch (Exception e) {System.out.println("jMusic Play error in pause thread");e.printStackTrace();}
                System.out.println(" Completed MIDI playback --------");
                if (exit) System.exit(0); // horrid but less confusing for beginners
            }});
        pauseThread.start();
    }
    
    public static void stopMidi() {
        if (pt != null) {
            pt.stopPlayThread();
            midiPlaying = false;
        }
    }	
	
	/**
    * Repeated playback the jMusic score JavaSound MIDI
     * @param Note The note to be played. See midiCycle(Score s)
     */
	public static void midiCycle(Note n) {
		Score s = new Score("One note score");
        s.addPart(new Part(new Phrase(n)));
        midiCycle(s);
	}	
    
	/**
     * Repeated playback the jMusic score JavaSound MIDI
     * @param Phrase The Phrase to be played. See midiCycle(Score s)
     */
	public static void midiCycle(Phrase phr) {
		Score s = new Score(phr.getTitle() + " score");
        s.addPart(new Part(phr));
        midiCycle(s);
	}
    
	/**
     * Repeated playback the jMusic score JavaSound MIDI
     * @param Part The Part to be played.  See midiCycle(Score s)
     */
	public static void midiCycle(Part p) {
		Score s = new Score(p.getTitle() + " score");
        s.addPart(p);
        midiCycle(s);
	}
    
	/**
     * Continually repeat-plays a Score object (i.e. loops).  If the Score object 
     * reference is altered, that alteration will be heard on the following cycle.
     * Score should be a minimum of 1 beat long.
     * NB: It takes a small amount of time to load the Score.  Due to the use of 
     * threads and a timer, no delay is heard between loops (i.e. the score is 
     * prepared just in time to be played).  However, this means that alterations 
     * made to the Score object near the end of the segment (ie final beats) may not
     * be heard for an extra loop.  Putting this another way, if a 1-bar Score is 
     * being looped, the next bar may be pre-loaded as the last beat is being played.
     * This means that any alterations made after this point will not appear in the 
     * very next loop.
     */
	public static void midiCycle(Score s){
		if (cyclePlaying == true) stopCycle();
        cyclePlaying = true;
        pc = new PlayCycle(s);
		new Thread(pc).start();
	}
	
    /**
     * Playback an audio file using Java Applet audioclip playback.
     * A audioClip limitation is that the file must be small enough to fit into RAM.
     * This method is compatibl with Java 1.1 and higher.
     * @param String The name of the file to be played.
     */ 
    public static void audioClip(String fileName) {
        System.out.println("-------- Playing an audio file ----------");
        System.out.println("Loading sound into memory, please wait...");
        java.io.File audioFile = new java.io.File(fileName);
        try {
            java.applet.AudioClip sound = java.applet.Applet.newAudioClip(audioFile.toURL());
            System.out.println("Playing '" + fileName + "' ...");
            sound.play();
        } catch (java.net.MalformedURLException e) {
            System.err.println("jMusic play.au error: malformed URL or filename");
        }
        try {
            // bytes, channels, sample rate, milliseconds, cautious buffer
            Thread.sleep((int)(audioFile.length() / 2.0 / 44100.0 / 2.0 * 1000.0) + 1000);
        } catch (InterruptedException e) {
            System.err.println("jMusic play.au error: Thread sleeping interupted");
        }
        System.out.println("-------------------- Completed Playback ----------------------");
        System.exit(0); // horrid but less confusing for beginners
	}
	
}


