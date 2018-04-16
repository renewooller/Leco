package com.lemu.leco.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jm.music.data.Score;
import jm.music.tools.Mod;
import ren.gui.components.DropTargetJPanel;
import ren.gui.components.TransportGUI;
import ren.gui.components.ext.PanelDropTarget;
import ren.gui.seqEdit.DraggableBTracker;
import ren.gui.seqEdit.ParamNTGC;
import ren.music.Player.Playable;
import ren.music.Player.PrePostPlayerEventListener;

@SuppressWarnings("serial")
public class ArrangePanel extends JPanel implements PrePostPlayerEventListener  {
	
	private int height = 800;
	private int width = 800;
	private int topMargin = 40;
	private int innerTopMargin = 15;
	//private LinkedList<ScoreBlock> scoreBlockList = new LinkedList();
	
	private ScorePane scorePane;
	private JScrollPane scrollPane;
	private JPanel scorePaneHolder;
	private ParamNTGC note2graph;
	private DraggableBTracker dbt;
	private Playable player;
	private TransportGUI transport;
	private JPanel zoomPanel;
	private PanelDropTarget panDroTar;
	private int marginInnerScroll = 5;
	private int marginInnerScoreHolder = 15;
	private int bw = 2;
	private double zf = 0.7;
	public ArrangePanel() {
		super();
	}
	
	public void construct(Playable player) {
		this.setPlayer(player);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(width, height));
		initBeatTracker();
		initTransport();
		constructZoom();
		
		constructView();
		
		constructBorders();
		
		//this.validate();
		this.validateTree();
		
	}
	
	private void constructBorders() {
		this.setBorder(BorderFactory.createLineBorder(Color.BLUE, bw));
		this.scrollPane.setBorder(BorderFactory.createLineBorder(Color.GREEN, bw));
		this.scorePaneHolder.setBorder(BorderFactory.createLineBorder(Color.RED, bw));
		this.scorePane.setBorder(BorderFactory.createLineBorder(Color.CYAN, bw));
		
	}
	
	private void constructView() {
		this.add(transport, 0);
		this.add(zoomPanel);
		
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setSize(new Dimension(this.width, this.height-topMargin));
		scrollPane.setLocation(0, topMargin);
		//scrollPane.setCorner(ScrollPaneConstants.LOWER_TRAILING_CORNER, constructZoom());
		
			scorePaneHolder = new JPanel();
			scorePaneHolder.setLayout(null);
			scorePaneHolder.setSize(new Dimension(this.width, scrollPane.getHeight()-marginInnerScroll));
			
				scorePane = new ScorePane();
				scorePane.setSize(new Dimension(this.width, scorePaneHolder.getHeight()-marginInnerScoreHolder-marginInnerScroll-bw*4 -1));
						//scorePaneHolder.getHeight()-innerTopMargin));
				scorePane.setLocation(0, marginInnerScoreHolder);
			//	scorePane.getInsets().top = 5;
			//	scorePane.getInsets().bottom = 5;
				
			scorePaneHolder.add(dbt, 0);	
			scorePaneHolder.add(scorePane);
		scrollPane.setViewportView(scorePaneHolder);
			
		this.add(scrollPane, 0);
	}
	
	private void constructZoom() {
		int butSize = 20;
		zoomPanel = new JPanel();
		JButton in = new JButton("+");
		in.setPreferredSize(new Dimension(butSize, butSize));
		in.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomIn();
			}
		});
		JButton out = new JButton("-");
		out.setPreferredSize(new Dimension(butSize, butSize));
		
		out.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomOut();
			}
		});
		
		zoomPanel.add(new JLabel("zoom x"));
		zoomPanel.add(in);
		zoomPanel.add(out);
		zoomPanel.setLocation(this.width-butSize*4-bw*2-100, 0);
		zoomPanel.setSize(zoomPanel.getLayout().preferredLayoutSize(zoomPanel));
	}
	
	protected void zoomOut() {
		double newZoom = note2graph.getPixelsPerBeat()*1.0*zf;
		newZoom = (newZoom < 0.1)? 0.1: newZoom;
		this.note2graph.setPixelsPerBeat(newZoom);
		System.out.println("zoom " +
				note2graph.getPixelsPerBeat() + 
				" sp " + scorePane.getWidth());
		lay();
	}

	protected void zoomIn() {
		this.note2graph.setPixelsPerBeat(note2graph.getPixelsPerBeat()*1.0/zf);
		lay();
	}
	
	private void initBeatTracker() {
		note2graph = new ParamNTGC(player.getBeatTracker().getScope());
		dbt = new DraggableBTracker();
		dbt.construct(note2graph, player);
		dbt.setLocation(0, 0);
	}
	
	private void initTransport() {
		transport = new TransportGUI();
		transport.construct(player);
		transport.setLocation(0, 0);
		transport.setSize(transport.getLayout().preferredLayoutSize(transport));
	}
	
	public void addScoreBlock(ScoreBlock sb) {
		sb.setPreferredSize(new Dimension(10, scorePane.getHeight()));
		this.scorePane.add(sb, 0);
		sb.setDropTarget(scorePane.getDropTarget());
		
		//scorePaneHolder.getBounds().width = scorePane.getPreferredSize().width;
//		Rectangle b = scorePaneHolder.getBounds();
//		scorePaneHolder.setBounds(b.x, b.y, 
//				scorePane.getPreferredSize().width, b.height);
		//scorePane.validate();
		lay();
		
		
	}

	private void lay() {
		scorePaneHolder.setPreferredSize(new Dimension(scorePane.getPreferredSize().width, 
				  scorePaneHolder.getHeight()));
		scorePane.setSize(new Dimension(scorePane.getPreferredSize().width, scorePane.getPreferredSize().height));
		scrollPane.validate();
		//scorePane.validate();
		//scorePaneHolder.validate();
		
	}
	
	public ParamNTGC getNote2Graph() {
		return note2graph;
	}
	
	public Playable getPlayer() {
		return player;
	}

	private void setPlayer(Playable player) {
		this.player = player;
		this.player.addPrePostPlayerEventListener(this);
	}

	public void preGoEventTriggered(Playable player) {
		player.setScore(this.scorePane.getScore());
	}
	
	public void postStopEventTriggered() {
		transport.reset();
	}
}

@SuppressWarnings("serial")
class ScorePane extends DropTargetJPanel {

	public ScorePane() {
		super();
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public Score getScore() {
		
		Component [] comps =  this.getComponents();
		if(comps.length == 0)
			return new Score("empty");
		
		Score score = ((ScoreBlock)comps[0]).getScore().copy();
		for(int i = 1; i < comps.length; i++) {
			
			Mod.append(score, ((ScoreBlock)comps[i]).getScore().copy());
		}
		return score;
	}

}
