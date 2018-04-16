package ren.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ren.gui.seqEdit.BeatListener;
import ren.music.Player.Playable;
import ren.util.JUtil;

public class TransportGUI extends JPanel implements BeatListener {

	private static final long serialVersionUID = 1L;
	private JLabel beatLab;
	private PlayPauseButton playButton;	
	private Playable player;
	private JButton stopButton;
	
	// for testing
	public static void mainT(String [] args) {
		JFrame jf = new JFrame();
		JPanel jp = new JPanel();
		TransportGUI tg = new TransportGUI();
		tg.construct(null);
		jp.add(tg);
		jf.getContentPane().add(jp);
		jf.pack();
		jf.setVisible(true);
		
		double [] problemDoubles = new double [] {0, 0.25, 1.0, 10.0, 2.0, 100.25, 3.0, 1000.25};
		for(int i=0; i<256; i++) {
			tg.beatFired(problemDoubles[i%problemDoubles.length]);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public TransportGUI() {super();}
	
	// called after stop
	public void reset() {
		this.playButton.setToPlay();
	}
	
	public void construct(Playable player) {
		this.player = player;
		this.player.getBeatTracker().addBeatListenerAuto(this);
		this.player.getBeatTracker().addUserModBeatListener(this);
		init();
	}
	
	public void init() {
		initBeatLab();
		initPlayPauseButton();
		initStopButton();
		initBorder();
	}
	
	private void initBeatLab() {
		beatLab = new JLabel();
		beatLab.setPreferredSize(new Dimension(60, 12));
		beatLab.setText("00.00");
		this.add(JUtil.wrapInJPanel(beatLab));
	}
	
	private void initPlayPauseButton() {
		playButton = new PlayPauseButton();
		playButton.setPreferredSize(new Dimension(12, 12));
		playButton.construct(player);
		this.add(JUtil.wrapInJPanel(playButton));
	}
	
	private void initStopButton() {
		stopButton = new JButton();
		stopButton.setIcon(new StopIcon());
		stopButton.setSize(12, 12);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopPressed();
			}
		});
		this.add(JUtil.wrapInJPanel(stopButton));
	}
	
	public void stopPressed() {
		this.player.stop();
	}
	
	private void initBorder() {
		this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
	
	}

	public void beatFired(double newBeat) {
		this.beatLab.setText(String.valueOf(newBeat));
	}
	
}
