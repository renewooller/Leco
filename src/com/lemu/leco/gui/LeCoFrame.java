package com.lemu.leco.gui;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import jm.midi.MidiParser;
import jm.midi.SMF;
import jm.music.data.Score;
import jm.music.tools.Mod;
import ren.gui.ExampleFileFilter;
import ren.gui.components.RJFrame;
import ren.io.Midi;
import ren.util.PO;

import com.lemu.leco.play.JSPlayer;

public class LeCoFrame extends RJFrame {

	JPanel main;
	ArrangePanel arrPan;

	public LeCoFrame() {
		super(true);
		main = new JPanel(new GridBagLayout());
		this.getContentPane().add(main);

		JSPlayer jsp = new JSPlayer();
		jsp.getBeatTracker().getScope().setValue(jsp.getBeatTracker().getScope().getMax());
		arrPan = new ArrangePanel();
		arrPan.construct(jsp);

		main.add(arrPan);

		getContentPane().add(main);
		pack();
		setVisible(true);
	}

	public void buildMenuBar() {
		super.buildMenuBar();
		addMenuItem("import", file, this);
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getActionCommand().equals("import")) {
			try {
				importMidiFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void importMidiFile() throws IOException {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("mid");
		filter.setDescription("midi 1.0 smf files");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadMidi(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void loadMidi(String path) throws IOException {
		loadMidi(path, Integer.MAX_VALUE);
	}
	
	public void loadMidi(String path, int limit) {
		String fs = System.getProperty("file.separator");
		String[] splitPath = path.split(fs);
		Score s = new Score(splitPath[splitPath.length - 1]);
		Midi.getInstance().read(s, path);
		
		PO.p("before cropping \n" + s.toString());
		
		//Mod.crop(s, 0.0, limit*1.0);
		//SMF smf = new SMF();
		//InputStream is = new FileInputStream(path);
//		is = new BufferedInputStream(is, 1024);
//		smf.read(is);
//		MidiParser.SMFToScore(s, smf);
		
		PO.p("after cropping \n" + s);
		
		ScoreBlock sb = new ScoreBlock(s);
		sb.construct(arrPan);
		arrPan.addScoreBlock(sb);
	}

}
