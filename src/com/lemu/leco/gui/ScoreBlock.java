package com.lemu.leco.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import jm.music.data.Part;
import jm.music.data.Score;
import ren.gui.components.DragSourceJPanel;
import ren.util.PO;

public class ScoreBlock extends DragSourceJPanel implements FocusListener, MouseListener, MouseMotionListener {

	private Score score;
	
	private double minEndTime = 2.0;
	
	private ArrangePanel arrp;
	
	private ArrayList<PartBlock> partBlocks = new ArrayList<PartBlock>(16);
	
	protected Color borderColor = Color.GREEN;
	protected Color brdCol_selected = Color.MAGENTA;
	
	protected JLabel lbl_name = new JLabel("empty");
	
	protected Border bdr_nonFocus = BorderFactory.createBevelBorder(BevelBorder.LOWERED, 
			borderColor, borderColor.darker().darker());
	
	protected Border bdr_Focus = BorderFactory.createBevelBorder(BevelBorder.RAISED, 
			brdCol_selected, brdCol_selected.darker().darker());
	
	private ScoreBlock thisScoreBlock = this;
	
	private int PARTLIMIT = 8;
	
	/**
	 * @param arrp - the Arrange panel which houses this ScoreBlock
	 */
	public ScoreBlock(){super();}
	
	public ScoreBlock(Score s ){
		super();
		this.setLayout(new GridLayout(17, 1));
		setScore(s);
	}
	
	
	public ScoreBlock construct(ArrangePanel arrp) {
		
		this.arrp = arrp;
		if(score == null) setScore(new Score());
		
		this.setBackground(Color.LIGHT_GRAY);
		//this.setLayout(null);
		this.setBorder(bdr_nonFocus);
		
		//this.lbl_name.setLocation(5, 5);
		this.lbl_name.setSize(new Dimension(this.getPreferredSize().width-5, 15));
		this.add(lbl_name, 0, 0);
		this.setFocusable(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addFocusListener(this);
		
		constructPartBlocks();
		
		return this;
	}
	
	public void constructPartBlocks() {
		if(score.getSize() > PARTLIMIT) {
			PO.p("score size bigger than "+PARTLIMIT+" parts " + score.getSize());
		}
		
		int addedPartCount = 0;
		
		for(int i=0; i<score.getSize() && i<PARTLIMIT; i++) {
			Part p = null;
			if(score != null) {
				p = score.getPart(i);
			}
			if(p != null) {
				PartBlock pb = new PartBlock(this, p);
				partBlocks.add(pb);
				this.add(pb, i+1, 1);
				addedPartCount++;
			}
		}
		
		
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		double et = score.getEndTime();
		if(et <= minEndTime) {
			System.out.println("[ScoreBlock] warning: score end time " + et + 
					"smaller than minimum score block size " + minEndTime);
		}
		et =  et > minEndTime ? et : minEndTime;
		d.width = (int)(et*this.arrp.getNote2Graph().getPixelsPerBeat());
		return d;
	}
	
	public Score getScore() {
		return score;
	}
	
	public void setScore(Score score) {
		this.score = score;
		this.lbl_name.setText(score.getTitle());
	}

	public void focusGained(FocusEvent arg0) {
		this.setBorder(this.bdr_Focus);
	}

	public void focusLost(FocusEvent arg0) {
		this.setBorder(this.bdr_nonFocus);
	}

	public void mouseClicked(MouseEvent arg0) {
		this.requestFocus();
	}

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void mouseDragged(MouseEvent arg0) {
		this.requestFocus();
	}

	public void mouseMoved(MouseEvent arg0) {
		
	}
	
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		System.out.println("getTransfDa");
		return this;
	}

	public DataFlavor[] getTransferDataFlavors() {
		System.out.println("getTransfDa");
		return null;
	}

	public boolean isDataFlavorSupported(DataFlavor arg0) {
		System.out.println("getTransfDa");
		return false;
	}
	
}
