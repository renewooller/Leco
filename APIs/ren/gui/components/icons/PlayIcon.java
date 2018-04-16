package ren.gui.components.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class PlayIcon implements Icon {
	private int height, width;

	public PlayIcon() {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		int height = c.getBounds().height;
		int width = c.getBounds().width;

		g.setColor(Color.GREEN);
		g.fillPolygon(new int[] { (int) (0), width, (int) (0) }, new int[] { 0,
				(int) (height * 0.5), height }, 3);
		g.dispose();

	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

}