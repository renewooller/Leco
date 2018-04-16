package ren.gui.components.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class PauseIcon implements Icon {
	private int height, width;

	public PauseIcon() {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		int height = c.getBounds().height;
		int width = c.getBounds().width;

		//STOP
		//g.setColor(Color.RED);
		//g.fillRect(0, 0, width, height);
		
		//PAUSE
		g.setColor(Color.YELLOW);
		g.fillRect((int)(width*(1.0/6.0)), 1, (int)(width*(2.0/6.0)), height-1);
		g.fillRect((int)(width*(4.0/6.0)), 1, (int)(width*(5.0/6.0)), height-1);
		
		
		//	g.fillRect((int)(width*0.15),(int)(height*0.15),
		//	   (int)(width*1.0-width*3.0), (int)(height*1.0-height*3.0));
		g.dispose();

	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

}