/*
 * Created on Mar 9, 2006
 *
 * @author Rene Wooller
 */
package com.lemu.gui.musicArea;

import java.awt.Color;
import java.awt.Graphics;

import com.lemu.music.Glomordel;

import jmms.TickEvent;

public class Glomor extends MGComponent {

	private Glomordel gmod;
	
	public Glomor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Glomor construct(Glomordel gmod, MusicArea ma) {
		super.construct(gmod, ma);
		this.gmod = gmod;
		return this;
	}

	public void paintComponent(Graphics g) {
		//PO.p("painting at "+ this.getX() + " " + this.getY());
		g.setColor(Color.GREEN);
		g.fillOval(0, 0, 40, 40);
		g.dispose();
		
	}

	/*
	public void addTuioObj(long sess_id, int id) {
		// TODO Auto-generated method stub
		
	}

	public void updateTuioObj(long sess_id, int id, 
			float x, float y, 
			float a, float X, float Y, float A, float m, float r) {
		// TODO Auto-generated method stub
	//	PO.p("x = " + x + " y = " + y);
		
		this.setLocation((int)((1.0-x)*super.area.getWidth()),
						 (int)((y-0.1)*super.area.getHeight()));
		this.repaint();
		
	}*/
	
	public void setLocation(int x, int y) {
		this.gmod.setMorphIndexes(x,y);
		
		super.setLocation(x, y);
	}
	

	public void removeTuioObj(long sess_id, int id) {
		// TODO Auto-generated method stub
		
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public void tick(TickEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void initUI() {
		// TODO Auto-generated method stub
		
	}

}
