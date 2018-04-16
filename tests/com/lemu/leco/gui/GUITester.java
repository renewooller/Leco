package com.lemu.leco.gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequencer.SyncMode;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jm.midi.MidiSynth;
import jm.music.data.Part;
import ren.gui.ParameterMap;
import ren.gui.components.NumTexFieldDouble;
import ren.gui.seqEdit.NotePanel;
import ren.util.PO;

import com.lemu.gui.LPartEditor;
import com.lemu.gui.MultiMorphEditor;
import com.lemu.gui.PMGEditor;
import com.lemu.gui.musicArea.MorphComponent;
import com.lemu.gui.musicArea.MusicArea;
import com.lemu.gui.musicArea.PatternComponent;
import com.lemu.leco.play.JSPlayer;
import com.lemu.music.LPart;
import com.lemu.music.MultiMorph;
import com.lemu.music.PatternMusicGenerator;
import com.lemu.music.TestMus;
import com.lemu.play.LPlayer;

/*
 * Created on 13/02/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 13/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class GUITester {
	LPlayer lp;// = new LPlayer();
    JFrame jf;
    TestMus testMus = new TestMus();
	//private MidiSynth msyn;
	JLabel lab;
	
	public static void main(String[] args) {
		new GUITester();
	}
	
	private void init() {
		initGui();
		initPlayer();
	}
	
	private void initPlayer() {
		lp = new LPlayer();
	}
	
	private void initGui() {
		jf = new JFrame();
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	public GUITester() {
		//init();
		
		//this.multiMorphEditor();
	
	//notePanel();

	//	lpartEditor();

		//cceditor();
		
		//pmgEditor();
		//initGui();
		//notePanel();
		arrangePanel();
		//midiGuiSynch();
	}
	
//	private void midiGuiSynch() {
//	//	msyn = new MidiSynth();
//		createStopWatchGui();
//	}
//	
//	private  void buttonClicked() {
//		
//		try {
//			msyn.play(testMus.score(8));
//			
//		} catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		}
//		
//	}
	
	
	/*
	private void createStopWatchGui() {
		Sequencer seq = msyn.getSequencer();
		//seq.setMasterSyncMode(SyncMode.MIDI_SYNC);
		
		JPanel p = new JPanel();
		JButton but = new JButton("click");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonClicked();
			}	
		});
		p.add(but);
		
		lab = new JLabel("0:0");
		p.add(lab);
		
		Thread thread = new Thread() {
			public void run() {
				while(true) {
					counter = msyn.getBeats();
					lab.setText("c " + counter);
					try {
						sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.start();
		
		jf.getContentPane().add(p);
		jf.pack();
		jf.setVisible(true);
	}
*/
	
	double counter = 0;
	int blockCount = 2;
	
	private void addBlock(ArrangePanel arrPan) {
		ScoreBlock block = (new ScoreBlock()).construct(arrPan);
		
		block.setScore(testMus.score((blockCount*4)));
		arrPan.addScoreBlock(block);
		blockCount++;
	}
	
	private void addBlock(ArrangePanel arrPan, String type) {
		if( type.equalsIgnoreCase("morph")) {
			MorphScoreBlock block = (new MorphScoreBlock()).construct(arrPan);
			block.setScore(testMus.score(16));
			arrPan.addScoreBlock(block);
			blockCount++;
		} else {
			addBlock(arrPan);
		}
	}
	
	private void arrangePanel() {
		
		//jf = new JFrame();
		initGui();
		
		JSPlayer jsp = new JSPlayer();
		
		JPanel innerPan = new JPanel();
		ArrangePanel arrPan = new ArrangePanel();//jsp);
		arrPan.construct(jsp);
		
		addBlock(arrPan);
		
		testMus.pitchOffset = 50;
		
		addBlock(arrPan, "Morph");
		
		addBlock(arrPan);
		
		arrPan.validate();
		
		innerPan.add(arrPan); 
		 
		jf.getContentPane().add(innerPan);
		jf.pack();
		jf.setVisible(true);
	}
	
	private void numTextField() {
		JFrame jf = new JFrame();
		
		NumTexFieldDouble ntf = (new NumTexFieldDouble()).constructThin(
				(new ParameterMap()).construct(0,8, 1, "test"));
		
		ntf.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
		
		ntf.setInsets(0);
		
		jf.getContentPane().add(ntf);
					
		jf.pack();
		jf.show();
	}
	
	private void cceditor() {
		JFrame jf = new JFrame();

		// ed.constructMulti(dmc, mm);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();

		ParameterMap pm = (new ParameterMap()).construct(0, 100, -1, 1, 1.0, "new paramMap");
		
		NumTexFieldDouble nd = (new NumTexFieldDouble()).construct(pm, 100);
		
		jp.add(nd);
		
		/*
		LPart lp = new LPart();
		lp.construct(new Part());

		PO.p("constructing ccedit");
		CCEditor cedit = (new CCEditor()).construct(lp);
*/
		PO.p("finished constructing");
	//	jp.add(cedit);
		jf.getContentPane().add(jp);
		
		jf.pack();
		jf.setVisible(true);
	}

	private void lpartEditor() {
		JPanel jp = new JPanel();
		
		LPart lp = new LPart();
		lp.construct(new Part());

		jp.add((new LPartEditor(lp)));
		
		jf.getContentPane().add(jp);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);
	}
	
	private void pmgEditor() {
		
		JPanel jp = new JPanel();
			
		//LPart lp = new LPart();
		//lp.construct(new Part());

		PatternMusicGenerator pmg1 = new PatternMusicGenerator();
		pmg1.constructPattern();
		
		PMGEditor ped = (new PMGEditor()).construct(pmg1, lp);
		
		jp.add(ped);
		
		jf.getContentPane().add(jp); // if it's in a jPanel it works on mac!
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);
	}

	private void notePanel() {

		JPanel jp = new JPanel();
		jp.add((new NotePanel()).construct());
		jf.getContentPane().add(jp);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));
		
		
		jf.pack();
		jf.setVisible(true);

	}

	private void multiMorphEditor() {
		MusicArea area = new MusicArea();

		PatternMusicGenerator pmg1 = new PatternMusicGenerator();
		pmg1.constructPattern();
		pmg1.constructPattern();

		PatternMusicGenerator pmg2 = new PatternMusicGenerator();
		pmg2.constructPattern();
		pmg2.constructPattern();

		MultiMorph mm = new MultiMorph();
		mm.constructMultiMorph(pmg1, pmg2);

		MorphComponent dmc = new MorphComponent();
		dmc.construct((new PatternComponent()).construct(0, 0, area, pmg1),
				(new PatternComponent()).construct(1, 1, area, pmg2), lp, area);

		JFrame jf = new JFrame();
		
		MultiMorphEditor ed = new MultiMorphEditor();
				
		
		ed.constructMulti(dmc, mm, jf);
		// ed.constructMulti(dmc, mm);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);

		jf.getContentPane().add(ed);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);

		/*
		 * JPanel jp = new JPanel();
		 * 
		 * LJSlider ljs = new LJSlider(1, 1, 10, 1);
		 * 
		 * //DefaultBoundedRangeModel dm = new DefaultBoundedRangeModel(1, 1, 1,
		 * 20); // ljs.setModel(dm); ljs.addMouseListener(new MouseAdapter() {
		 * public void mouseEntered(MouseEvent e) { PO.p("entered"); } public
		 * void mouseExited(MouseEvent e) { PO.p("exited"); } });
		 * 
		 * jp.add(ljs);
		 */

	}

}
