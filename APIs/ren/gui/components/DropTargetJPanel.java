package ren.gui.components;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JPanel;

import ren.util.PO;

public class DropTargetJPanel extends JPanel implements DropTargetListener {

	public DropTargetJPanel() {
		super();
		
		DropTarget dropTarget = new DropTarget(
				this, DnDConstants.ACTION_MOVE, this, true);
		
		this.setDropTarget(dropTarget);
	}

	public void dragEnter(DropTargetDragEvent arg0) {
		PO.p("dnd1");
		
	}

	public void dragExit(DropTargetEvent arg0) {
		PO.p("dnd2");
		
	}

	public void dragOver(DropTargetDragEvent arg0) {
		PO.p("dnd3");
		
	}

	public void drop(DropTargetDropEvent arg0) {
		PO.p("dnd4");
		
	}

	public void dropActionChanged(DropTargetDragEvent arg0) {
		PO.p("dnd5");
		
	}
	
	
	
}
