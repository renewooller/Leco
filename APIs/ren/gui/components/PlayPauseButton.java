package ren.gui.components;

import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import ren.gui.components.icons.PauseIcon;
import ren.music.Player.Playable;

public class PlayPauseButton extends JCheckBox implements ActionListener {

	private static final long serialVersionUID = 1L;
	private transient Playable player;

	public PlayPauseButton() {
		super();
	}

	public PlayPauseButton construct(Playable player) {
		this.player = player;
		this.setIcon(new PlayIcon());
		this.setSelectedIcon(new PauseIcon());
		this.addActionListener(this);
		return this;
	}
	
	public void setPlayer(Playable player) {
		this.player = player;
	}
	
	public void setToPlay() {
		this.setSelected(false);
	}
	
	public void setToPause() {
		this.setSelected(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (((JCheckBox) e.getSource()).isSelected()) {
			
			Thread thr = new Thread() {
				public void run() {
					goPlayer();
				}
			};
			thr.start();
		} else {
		
			Thread thr = new Thread() {
				public void run() {
					pausePlayer();
				}
			};
			thr.start();
		}
	}
	
	private void goPlayer() {
		player.go();
	}
	
	private void pausePlayer() {
		player.pause();
	}

	public boolean isStopped() {
		return this.isSelected();
	}

	
}



