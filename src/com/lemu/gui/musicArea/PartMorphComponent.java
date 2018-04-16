/*
 * Created on May 25, 2006
 *
 * @author Rene Wooller
 */
package com.lemu.gui.musicArea;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import ren.gui.DraggableComponent;

public class PartMorphComponent extends DraggableComponent {
	
	public static final int mx = 640;//320;
	public static final int my = 480;//240;
	public static int wic = 40; // estimated width of counter
	private static int snax = 20; // snap to side (0 or 1) factor
	
	public PartMorphComponent() {
		super();
		
	}
	
	public PartMorphComponent construct() {
		return (PartMorphComponent)super.construct(0, 0);
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		if (mo)
			g.setColor(Color.GRAY);
		if (md)
			g.setColor(Color.GREEN);

		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.CYAN);
		g.setFont(new Font("serif", 0, 15)); 
		
		g.dispose();
	}
	
}
