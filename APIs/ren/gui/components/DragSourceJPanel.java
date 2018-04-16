package ren.gui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;

import javax.swing.JPanel;

import ren.util.PO;

public class DragSourceJPanel extends JPanel implements DragSourceListener,
		DragGestureListener, Transferable {

	private Image dragImage;
	
	public DragSourceJPanel() {
		DragSource dragSource = DragSource.getDefaultDragSource();
	    dragSource.createDefaultDragGestureRecognizer(this, // What component
	        DnDConstants.ACTION_COPY_OR_MOVE, // What drag types?
	        this);// the listener
	    
	}
	
	private void updateDragImage(Graphics2D g) {
		
    	Color c = new Color(0.5f, 0.0f, 0.0f, 0.1f);
    	
    	g.setColor(c);//new Color(0.1f, 0.0f, 0.0f, 0.0f));
    	g.fillRect(0, 0, this.getWidth(), this.getHeight());
    	
    	//g.setColor(Color.BLACK);
    	
    //	g.translate(-this.getX(), -this.getY());
    //	g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
    	//g.drawRect(1, 1, this.getWidth()-3, this.getHeight()-3);
    	//g.drawRect(2, 2, this.getWidth()-5, this.getHeight()-5);
	}
	
	public void dragDropEnd(DragSourceDropEvent arg0) {
		PO.p("dragSourcePanel - drag1");

	}

	public void dragEnter(DragSourceDragEvent arg0) {
		PO.p("dragSourcePanel - drag2");

	}

	public void dragExit(DragSourceEvent arg0) {
		PO.p("dragSourcePanel - drag3");

	}

	public void dragOver(DragSourceDragEvent e) {
		PO.p("dragSourcePanel - drag4");
		this.updateDragImage((Graphics2D)this.dragImage.getGraphics());
		//this.getParent().p(e.getLocation().x, e.getLocation().y, this.getWidth(), this.getHeight());
	}

	public void dropActionChanged(DragSourceDragEvent arg0) {
		PO.p("dragSourcePanel - drag5");

	}

	public void dragGestureRecognized(DragGestureEvent e) {
		PO.p("dragSourcePanel - drag6");
		PO.p("e.getDragAction() = " + e.getDragAction());
		Cursor cursor;
        switch (e.getDragAction()) {
        case DnDConstants.ACTION_COPY:
        	PO.p("copy drop");
        	cursor = DragSource.DefaultCopyDrop;
          break;
        case DnDConstants.ACTION_MOVE:
        	PO.p("move drop");
          cursor = DragSource.DefaultMoveDrop;
          break;
        default:
          return; // We only support move and copys
        }
        
        if(DragSource.isDragImageSupported()) {
        	dragImage = this.createImage(this.getWidth(), this.getHeight());
        	Graphics2D g = (Graphics2D)dragImage.getGraphics();
        	updateDragImage(g);
        	Point hotspot = new Point(-e.getDragOrigin().x, -e.getDragOrigin().y);
        	
        	// while this is OK, it will be better to use a glass pane style custom system I think, 
        	// similar to this:
        	//http://codeidol.com/java/swing/Drag-and-Drop/Translucent-Drag-and-Drop/
        	
        	e.startDrag(cursor, dragImage, hotspot, this, this);
        	
        } else {
        	e.startDrag(cursor, this,  this);
        }
        
	}

	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		System.out.println("transferable data");
		return this;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return null;
	}

	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return false;
	}

}
