/*
 *  Copyright (c) 2008 by Andrew R. Brown
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package arb.soundciphermod;

import javax.sound.midi.*;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Class;
import java.lang.reflect.Method; //import processing.core.PApplet;
import arb.soundcipher.constants.ProgramChanges;
import arb.soundcipher.constants.PitchClassSets;
import arb.soundcipher.constants.DrumMap;
import arb.soundcipher.constants.MidiMessageTypes;
import java.util.List;

/**
 * The <code>SCScore<code> class provides music data structure and 
 * scheduling services. It is part 
 * of the {@link SoundCipher} library for 
 * <a href="http://processing.org">Processing</a>.
 * A score can contain notes, phrases (note sequences), chords 
 * (note clusters), or 'callbacks.' A score can be played back once, 
 * repeated or looped.
 *
 * Notes in a score play back using the internal JavaSound soundbank, 
 * MIDI Events (note messages) are sent to the active MIDI device, 
 * while Scheduled Events provide a callback into Processing for any 
 * arbitary synchronised purpose (e.g. audio file playback, drawing, 
 * sound synthesis parameter control, and OSC or MIDI message sending).
 *
 * When using a SCScore instance always include a stop() method in your 
 * Processing program to halt playback when the program exits. 
 * Like so, assuming your SCScore instance is called score.<br>
 * void stop() {<br>
 *   score.stop();<br>
 * }<br>
 * 
 * @author <a href="http://www.explodingart.com/arb/">Andrew R. Brown</a>
 */

public class SCScore extends SCUtilities implements MetaEventListener,
		ProgramChanges, DrumMap, PitchClassSets, MidiMessageTypes {
	/** A JavaSound Sequencer instance. */
	protected Sequencer sequencer;
	/** A JavaSound Sequence instance. */
	private Sequence seq;
	/** The default tempo to the score, 120.0 beats per minute. */
	public double tempo = 120.0; // bpm
	/**
	 * The default number of repeats on score playback, 0. -1 indicates infinite
	 * repeats (looping).
	 */
	public double repeat = 0; // -1 is infinite repeat
	/** The number of repeats a score has played */
	public double repeatCounter = 0;
	/** The number of MIDI pulses per quater note */
	private int resolution = 240; // ppqn
	/** A flag to indicate if the score is currently playing. */
	public boolean playing = false;
	/** A flag to indicate if the class should print vervose debug info. or not. */
	private boolean debug = false;
	/**
	 * Specifies the default instrument to 0 [0 - 127] (0 = piano on the
	 * JavaSound synthesizer)
	 */
	public double instrument = 0;
	/** Specifies the default MIDI channel to 0 [0-15] */
	public double channel = 0;
	/** Specifies the default pan poisition to be 64, in the centre [0 - 127] */
	public double pan = 64;
	/**
	 * Specifies the amount of duration that will sound, e.g., 0.2 = stacatto,
	 * 1.0 = legato
	 */
	public double articulation = 0.8;

	/** The link back to Processing. */
	// private static PApplet pApplet;
	/**
	 * Creates a empty SoundCipher Score (SCScore) object. A SCScore is an
	 * enhanced and easy to use wrapper for a JavaSound sequencer.
	 */
	public SCScore() {
		if (debug)
			System.out.println("SoundCipher Score constructor");
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.addMetaEventListener(this);
			seq = new Sequence(Sequence.PPQ, resolution);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the Score playback.
	 */
	public void play() {
		this.play(this.repeat, this.tempo);
	}

	/**
	 * Start the Score playback.
	 * 
	 * @param repeat
	 *            The number of repeats (-1 = infinite)
	 */
	public void play(double repeat) {
		this.play(repeat, this.tempo);
	}

	/**
	 * Start the Score playback.
	 * 
	 * @param repeat
	 *            The number of repeats (-1 = infinite)
	 * @param tempo
	 *            The playback speed in beats per minute
	 */
	public void play(double repeat, double tempo) {
		if (debug)
			System.out.println("Playing ... repeat = " + repeat);
		// if (playing) this.stop();
		this.repeat = repeat;
		this.tempo = tempo;
		this.repeatCounter = 0;
		update();
		sequencer.setMicrosecondPosition(0l);
		sequencer.setTempoInBPM((float) tempo);
		sequencer.start();
		playing = true;
	}

	/**
	 * Halt the score playback.
	 */
	public void stop() {
		if (debug)
			System.out.println("stop");
		sequencer.stop();
		playing = false;
	}

	/**
	 * Refresh the sequence data and start time of the score.
	 * 
	 * An update is required if data is dynamically added to the score while it
	 * is playing.
	 */
	public void update() {
		if (debug)
			System.out.println("update");
		try {
			sequencer.setTempoInBPM((float) tempo); // working??
			sequencer.setSequence(seq);
		} catch (javax.sound.midi.InvalidMidiDataException e) {
			e.printStackTrace();
		}
		if (repeat >= 0) {
			sequencer.setLoopCount((int) repeat);
		}
	}

	/**
	 * Remove all data from the score.
	 */
	public void empty() {
		if (debug)
			System.out.println("empty");
		if (sequencer != null) {
			if (seq != null) {
				Track[] tracks = seq.getTracks();
				for (int j = tracks.length; j > 0; j--) {
					seq.deleteTrack(tracks[j - 1]);
					tracks[j - 1] = null;
				}
			}
			this.update();
		}
	}

	/**
	 * Specifies the default instrument number [0-127].
	 */
	public void instrument(double instrumentNumber) {
		this.instrument = instrumentNumber;
	}

	/**
	 * Specify the speed (tempo) of the score in beats per minute
	 * 
	 * @param newTempo
	 *            The new tempo value.
	 */
	public void tempo(double newTempo) {
		this.tempo = newTempo;
		sequencer.setTempoInBPM((float) tempo);
	}

	/**
	 * Specify the number of repetitions for the score (-1 = infinite loop)
	 * 
	 * @param repeat
	 *            The new repeat value.
	 */
	public void repeat(double repeat) {
		this.repeat = repeat;
	}

	/**
	 * A conditional that indicates if the score is currently playing.
	 * 
	 * @return True or False
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * Retreive the Sequencer object used by this SCScore.
	 * 
	 * @return A JavaSound sequencer object
	 */
	public Sequencer sequencer() {
		return this.sequencer;
	}

	/**
	 * Schedules a single note in the score.
	 * 
	 * This reduced-argument version of the method can be useful for creating
	 * scores where all notes are on a same MIDI channel, instrument,
	 * articulation and pan settings.
	 * 
	 * @param startBeat
	 *            Specifies when the note will play, in beats, after the score
	 *            starts
	 * @param pitch
	 *            The MIDI pitch (frequency) at which the note will play [0-127]
	 * @param dynamic
	 *            The loudness, MIDI velocity, of the note [0-127]
	 * @param duration
	 *            The length that the note will sound, in beats
	 */
	public void addNote(double startBeat, double pitch, double dynamic,
			double duration) {
		addNote(startBeat, this.channel, this.instrument, pitch, dynamic,
				duration, this.articulation, this.pan);
	}

	/**
	 * Schedules a single note in the score.
	 * 
	 * @param startBeat
	 *            Specifies when the note will play, in beats, after the score
	 *            starts
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for this note [0-127]
	 * @param pitch
	 *            The MIDI pitch (frequency) at which the note will play [0-127]
	 * @param dynamic
	 *            The loudness, MIDI velocity, of the note [0-127]
	 * @param duration
	 *            The length that the note will sound, in beats
	 * @param articulation
	 *            An articulation multiplier for duration (0.8 by default)
	 * @param pan
	 *            The note's left-right location in the stereo field [0-127]
	 */
	public void addNote(double startBeat, double channel, double instrument,
			double pitch, double dynamic, double duration, double articulation,
			double pan) {
		addPhrase(startBeat, channel, instrument, new double[] { pitch },
				new double[] { dynamic }, new double[] { duration },
				new double[] { articulation }, new double[] { pan });
	}

	/**
	 * Schedules a note sequence (phrase) within the score.
	 * 
	 * @param startBeat
	 *            Specifies when the phrase will start to play, in beats, after
	 *            the score starts
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for this note [0-127]
	 * @param pitches
	 *            The MIDI pitches (frequencies) at which the note will play
	 *            [0-127]
	 * @param dynamics
	 *            The loudness, MIDI velocity, of each note [0-127]
	 * @param durations
	 *            The lengths that each note will sound, in beats
	 * @param articulations
	 *            An articulation multiplier for each duration (0.8 by default)
	 * @param pans
	 *            Each note's left-right location in the stereo field [0-127]
	 */
	public void addPhrase(double startBeat, double channel, double instrument,
			float[] pitches, float[] dynamics, float[] durations,
			float[] articulations, float[] pans) {
		double[] dPitches = new double[pitches.length];
		double[] dDynamics = new double[pitches.length];
		double[] dDurations = new double[pitches.length];
		double[] dArticulations = new double[pitches.length];
		double[] dPans = new double[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			dPitches[i] = pitches[i];
			dDynamics[i] = dynamics[i];
			dDurations[i] = durations[i];
			dArticulations[i] = articulations[i];
			dPans[i] = pans[i];
		}
		this.addPhrase(startBeat, channel, instrument, dPitches, dDynamics,
				dDurations, dArticulations, dPans);
	}

	/**
	 * Schedules a note sequence (phrase) within the score.
	 * 
	 * @param startBeat
	 *            Specifies when the phrase will start to play, in beats, after
	 *            the score starts
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for this note [0-127]
	 * @param pitches
	 *            The MIDI pitches (frequencies) at which the note will play
	 *            [0-127]
	 * @param dynamics
	 *            The loudness, MIDI velocity, of each note [0-127]
	 * @param durations
	 *            The lengths that each note will sound, in beats
	 * @param articulations
	 *            An articulation multiplier for each duration (0.8 by default)
	 * @param pans
	 *            Each note's left-right location in the stereo field [0-127]
	 */
	public void addPhrase(double startBeat, double channel, double instrument,
			double[] pitches, double[] dynamics, double[] durations,
			double[] articulations, double[] pans) {
		double endBeat = startBeat;
		for (int i = 0; i < durations.length; i++) {
			endBeat += durations[i];
		}
		try {
			buildSequence(startBeat, channel, instrument, pitches, dynamics,
					durations, articulations, pans, false);
			// add event to round off the sequence
			Track[] tracks = seq.getTracks();
			tracks[0].add(createEvent(ShortMessage.POLY_PRESSURE,
					(int) channel, 0, 0, (long) (endBeat * resolution)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schedules a note cluster (chord) within the score.
	 * 
	 * @param startBeat
	 *            Specifies when the notes will play, in beats, after the score
	 *            starts
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for this note [0-127]
	 * @param pitches
	 *            The MIDI pitches (frequencies) for each note [0-127]
	 * @param dynamic
	 *            The loudness, MIDI velocity, of the notes [0-127]
	 * @param duration
	 *            The length that the notes will sound, in beats
	 * @param articulation
	 *            An articulation multiplier for duration (0.8 by default)
	 * @param pan
	 *            The note's left-right location in the stereo field [0-127]
	 */
	public void addChord(double startBeat, double channel, double instrument,
			float[] pitches, double dynamic, double duration,
			double articulation, double pan) {
		double[] dPitches = new double[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			dPitches[i] = (double) pitches[i];
		}
		this.addChord(startBeat, channel, instrument, dPitches, dynamic,
				duration, articulation, pan);
	}

	/**
	 * Schedules a note cluster (chord) within the score.
	 * 
	 * @param startBeat
	 *            Specifies when the notes will play, in beats, after the score
	 *            starts
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for this note [0-127]
	 * @param pitches
	 *            The MIDI pitches (frequencies) for each note [0-127]
	 * @param dynamic
	 *            The loudness, MIDI velocity, of the notes [0-127]
	 * @param duration
	 *            The length that the notes will sound, in beats
	 * @param articulation
	 *            An articulation multiplier for duration (0.8 by default)
	 * @param pan
	 *            The note's left-right location in the stereo field [0-127]
	 */
	public void addChord(double startBeat, double channel, double instrument,
			double[] pitches, double dynamic, double duration,
			double articulation, double pan) {
		double[] dynamics = new double[pitches.length];
		double[] durations = new double[pitches.length];
		double[] articulations = new double[pitches.length];
		double[] pans = new double[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			dynamics[i] = dynamic;
			durations[i] = duration;
			articulations[i] = articulation;
			pans[i] = pan;
		}
		try {
			buildSequence(startBeat, channel, instrument, pitches, dynamics,
					durations, articulations, pans, true);
			// add event to round off the sequence
			Track[] tracks = seq.getTracks();
			tracks[0].add(createEvent(ShortMessage.POLY_PRESSURE,
					(int) channel, 0, 0,
					(long) ((startBeat + duration) * resolution)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schedules an callback event within the score.
	 * 
	 * @param startBeat
	 *            Specifies when the callback will be triggered, in beats, after
	 *            the score starts
	 * @param callbackID
	 *            The identifier for this event, which is passed as part of the
	 *            callback
	 */
	public void addCallback(double startBeat, int callbackID) {
		Track track = seq.createTrack();
		long startTime = (long) (startBeat * resolution);
		try {
			MetaMessage mm = new MetaMessage();
			mm.setMessage(70,
					new byte[] { new Integer(callbackID).byteValue() }, 1);
			track.add(new MidiEvent(mm, startTime));
		} catch (javax.sound.midi.InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates notes and schedules them for playback within a JavaSound
	 * Sequence.
	 * 
	 * @param startBeat
	 *            Specifies when the notes will play, in beats, within the
	 *            sequence
	 * @param channel
	 *            The MIDI channel to use for these notes [0-15]
	 * @param instrument
	 *            The JavaSound instrument (sound) to use for these notes
	 *            [0-127]
	 * @param pitches
	 *            The MIDI pitches (frequencies) at which the notes will play
	 *            [0-127]
	 * @param dynamics
	 *            The loudness (MIDI velocity) of each note [0-127]
	 * @param durations
	 *            The length that each note will sound, in beats
	 * @param articulations
	 *            An articulation multiplier for each duration (0.8 by default)
	 * @param pans
	 *            Each note's left-right location in the stereo field [0-127]
	 */
	private void buildSequence(double startBeat, double chan, double inst,
			double[] pitches, double[] dynamics, double[] durations,
			double[] articulations, double[] pans, boolean isChord) {
		Track track = seq.createTrack();
		// insert program change
		track.add(createEvent(ShortMessage.PROGRAM_CHANGE, (int) chan,
				(int) inst, 0, (long) 0.0));
		// add note events to track
		double currBeat = startBeat;
		for (int i = 0; i < pitches.length; i++) {
			if (debug)
				System.out.println("Pitch " + pitches[i]);
			addMidiEvents(track, chan, currBeat, pitches[i], dynamics[i],
					durations[i], articulations[i], pans[i]);
			if (isChord)
				currBeat += 0;
			else
				currBeat += durations[i];
		}
		if (debug)
			System.out.println("===========");
	}

	/**
	 * Schedules data for a single note (note on and off messages) to a track in
	 * the score.
	 * 
	 * @param track
	 *            The JavaSound track to which the events should added
	 * @param channel
	 *            The MIDI channel to use for this note [0-15]
	 * @param startBeat
	 *            Specifies when the notes will play, in beats, after the score
	 *            starts
	 * @param pitch
	 *            The MIDI pitch (frequency) for the note [0-127]
	 * @param dynamic
	 *            The loudness, MIDI velocity, of the notes [0-127]
	 * @param duration
	 *            The length that the notes will sound, in beats
	 * @param articulation
	 *            An articulation multiplier for duration (0.8 by default)
	 * @param pan
	 *            The note's left-right location in the stereo field [0-127]
	 */
	private void addMidiEvents(Track track, double chan, double startBeat,
			double pitch, double dynamic, double duration, double articulation,
			double pan) {
		long startTime = (long) (startBeat * resolution);
		long dur = (long) (duration * articulation * resolution);
		int bPitch = Math.max((int) pitch, -1); // -1 is a rest
		bPitch = Math.min(bPitch, 127);
		int bDyn = Math.max((int) dynamic, 0);
		bDyn = Math.min(bDyn, 127);
		if (bPitch >= 0) {
			// pan
			track.add(createEvent(ShortMessage.CONTROL_CHANGE, (int) chan, 10,
					(int) pan, startTime));
			// note
			track.add(createEvent(ShortMessage.NOTE_ON, (int) chan, bPitch,
					bDyn, startTime));
			track.add(createEvent(ShortMessage.NOTE_OFF, (int) chan, bPitch, 0,
					startTime + dur));
		}
	}

	/**
	 * Creates a single MIDI message and adds it to the score.
	 * 
	 * @param startBeat
	 *            Specifies when the message will be sent, in beats, after the
	 *            score starts
	 * @param type
	 *            The MIDI message status value; the type of message to add
	 *            (control change, pitch bend etc.)
	 * @param channel
	 *            The MIDI channel to use for this message [0-15]
	 * @param val1
	 *            The first byte of data (e.g., control change number)
	 * @param val2
	 *            The second byte of data (e.g., control change value)
	 */
	public void addMIDIMessage(double startBeat, int type, double channel,
			double val1, double val2) {
		Track track = seq.createTrack();
		track.add(createEvent(type, (int) channel, (int) val1, (int) val2,
				(long) (startBeat * resolution)));
	}

	/**
	 * Create an individual MIDI message event.
	 * 
	 * @param type
	 *            The message type (note on, note off etc.)
	 * @param chan
	 *            The MIDI channel to use for this event [0-15]
	 * @param val1
	 *            The first data byte value
	 * @param val2
	 *            The second data byte value
	 * @param tick
	 *            The time for the event to be scheduled in PPQN since the start
	 *            of the track
	 */
	private MidiEvent createEvent(int type, int chan, int val1, int val2,
			long tick) {
		ShortMessage bMessage = new ShortMessage();
		MidiEvent bEvent = null;
		try {
			bMessage.setMessage(type, chan, val1, val2);
			bEvent = new MidiEvent(bMessage, tick);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bEvent;
	}

	/**
	 * Read a MIDI file into a SoundCipher score
	 * 
	 * @param filePath
	 *            Path and name of the MIDI file. Absolute file path required.
	 */
	public void addMidiFile(String filePath) {
		try {
			File file = new File(filePath);
			try {
				Sequence tempSeq = MidiSystem.getSequence(file);
				if (debug)
					System.out
							.println("seq div type: " + seq.getDivisionType());
				if (debug)
					System.out.println("tempSeq res: "
							+ tempSeq.getResolution() + " Div type: "
							+ tempSeq.getDivisionType());
				Track[] tracks = tempSeq.getTracks();
				for (int i = 0; i < tracks.length; i++) {
					Track t = seq.createTrack();
					for (int j = 0; j < tracks[i].size(); j++) {
						t.add(tracks[i].get(j));
					}
				}
			} catch (javax.sound.midi.InvalidMidiDataException imde) {
				imde.printStackTrace();
			}
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read a MIDI file input stream into a SoundCipher score
	 * 
	 * @param is
	 *            The InputStream for the MIDI file.
	 */
	public void addMidiStream(InputStream is) {
		try {
			Sequence tempSeq = MidiSystem.getSequence(is);
			if (debug)
				System.out.println("seq div type: " + seq.getDivisionType());
			if (debug)
				System.out.println("tempSeq res: " + tempSeq.getResolution()
						+ " Div type: " + tempSeq.getDivisionType());
			Track[] tracks = tempSeq.getTracks();
			for (int i = 0; i < tracks.length; i++) {
				Track t = seq.createTrack();
				for (int j = 0; j < tracks[i].size(); j++) {
					t.add(tracks[i].get(j));
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Save the score as a MIDI file
	 * 
	 * @param filePath
	 *            Path and name of the MIDI file to be written. Absolute file
	 *            path required
	 */
	public void writeMidiFile(String filePath) {
		try {
			File file = new File(filePath);
			MidiSystem.write(seq, 1, file);
			if (debug)
				System.out.println("MIDI file written");
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Specify the MIDI device to be used for score playback.
	 * 
	 * @param deviceNumber
	 *            The id of the MIDI output as returned from getMidiDeviceInfo()
	 */
	public void setMidiDeviceOutput(int deviceNumber) {
		try {
			System.out.println("=== Opening MIDI output device ====");
			MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
			for (int i = 0; i < midiDeviceInfo.length; i++) {
				if (i != deviceNumber
						&& MidiSystem.getMidiDevice(midiDeviceInfo[i]).isOpen()) {
					MidiSystem.getMidiDevice(midiDeviceInfo[i]).close();
				}
			}
			MidiSystem.getMidiDevice(midiDeviceInfo[deviceNumber]).open();
			MidiSystem.getTransmitter().setReceiver(
					MidiSystem.getMidiDevice(midiDeviceInfo[deviceNumber])
							.getReceiver());
			sequencer.getTransmitter().setReceiver(
					MidiSystem.getMidiDevice(midiDeviceInfo[deviceNumber])
							.getReceiver()); // working?
			System.out.println("MIDI output is now directed to: "
					+ midiDeviceInfo[deviceNumber].getName());

		} catch (javax.sound.midi.MidiUnavailableException mue) {
			System.out.println("SoundCipher error: in setMidiDeviceOutput() ");
			mue.printStackTrace();
		}
	}

	/**
	 * The MetaMessage callback handler. Parses callback from events when the
	 * score is played.
	 * 
	 * @param message
	 *            The MetaMessage object passed from JavaSound during sequence
	 *            playback
	 */

	public void meta(MetaMessage message) {
		if (message.getType() == 47) { // 47 is end of track
			if (repeat == -1) {
				repeatCounter = repeatCounter + 1;
				sequencer.setMicrosecondPosition(0l);
				sequencer.setTempoInBPM((float) this.tempo);
				sequencer.start();
			} else
				playing = false;
		}
		if (message.getType() == 70) {
		}
	}

}