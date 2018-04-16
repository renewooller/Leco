package ren.env;import java.awt.Color;import java.awt.Dimension;import java.awt.Graphics;import java.awt.event.MouseEvent;import java.awt.event.MouseListener;import java.awt.event.MouseMotionListener;import java.io.Serializable;import java.util.Enumeration;import javax.swing.JFrame;import javax.swing.JPanel;import ren.gui.ParamEvent;import ren.gui.ParamListener;import ren.gui.ParameterMap;/***	@author Rene Wooller*	@version 8/10/02*/public class GraphView extends JPanel implements MouseListener, MouseMotionListener, Serializable{	public static void main(String [] args) {                JFrame jf = new JFrame();		GraphView tg = new GraphView();		tg.construct();                jf.getContentPane().add(tg);                jf.pack();                jf.setVisible(true);	}	//protected int spacing = 3;  //spacing aling the x axis	//protected int res = 3;	//X	//resolutoion (spacing along the y axis)	protected double stWin;	protected double enWin;		    protected int x = 0;     // variables that track the movement of the mouse	protected int y = 0;		protected final int r = 5; // radius of nodes	protected int offset;  //X	protected Node selectedNode;	protected ValueGraphModel nodeVector;	protected Node [] adj; // the adjacent nodes to the selected nodes 0 left 1 right	protected Node overNode; // - the node that covers the hiden nodes//	protected Dimension d = new Dimension(800, 400);	//protected Dimension dr; 	int pos;		/// pixels per beat	public final int DPPB = 40; // default pixels per beat	protected ParameterMap ppb = (new ParameterMap()).construct(1, 200, DPPB, "zoom");		public final double DFB = 16.0; // default number of beats	protected final double MAXB = 2048.0; // maximum window	public final int DFH = 200; // default height	public final Dimension DFDIM = new Dimension((int)(DPPB*DFB + 0.5), DFH);			public GraphView() {		super();		}		/**	 * 	 * @param length the lenght (in pixels) that the default size (not zoomed) 	 * 				of the graph	 * 			 * @param height default height	 * @param windowStart the starting point of the window onto the values	 * 		  			   (in beats)	 * @param windowFinish the end point of the window onto the values 	 * 					   (in beats) - this is like scope	 * @return	 */	public GraphView construct(int length, int height, 						   double windowStart, double windowFinish,						   ValueGraphModel vgm) {	//	PO.p("length = " + length);						setSizes(new Dimension(length, height));		this.setBackground(Color.green);		this.addMouseListener(this);		this.addMouseMotionListener(this);		this.setWindowOntoModel(windowStart, windowFinish);						offset = (int)(((Math.sqrt(1.0*(r*r + r*r)))/2) + 0.5);		nodeVector = vgm;		ppb.addParamListener(new ParamListener() {			public void paramFired(ParamEvent e) {				repaint();			}		});		//res = (int)(height/range + 0.5);	//	PO.p("this.width = " + this.getWidth());		return this;	}		public GraphView construct() {		return construct((int)(DFB*ppb.getValue()), DFH, 0, MAXB, 				(new ValueGraphModel()).construct(0, 127));	}	/*	public Dimension getDFSize() {		return new Dimension((int)(this.DFB*this.ppb.getValue()), this.dr.height);	}*/		public void setSizes(Dimension dr) {		this.setPreferredSize(dr);		this.setMaximumSize(dr);		this.setSize(dr);	}	/*	public void scaleSize(double xf, double yf) {		dr.width = (int)(dr.width*xf);		dr.height = (int)(dr.height*yf);		setSizes();		repaint();	}*/		public void setWindowOntoModel(double sw, double enw) {		this.stWin = sw;		this.enWin = enw;	}		public ValueGraphModel getModel() {		return this.nodeVector;	}		public void setModel(ValueGraphModel model) {		this.nodeVector = model;			}		/**	*	The paint method will paint the nodes and line using the int []s returned from the ValueGraphModel,	*	*	if the first node to draw is not on the edge of the screen, then it will draw from	*	(0, firstNode.y) to (firstNode.x, firstNode.y) likewise with the last node	*		*/	public void paint(Graphics g) {		super.paint(g);		g.setColor(Color.black);				Enumeration enumr = nodeVector.getEnumeration();				nodeVector.updateBegEnd();		Node n1 = nodeVector.beg();		Node n2 = nodeVector.end(); 				int i =0;		while(enumr.hasMoreElements()) {			i++;			n2 = (Node)enumr.nextElement();			drawDot(xpix(n2), ypix(n2), g, r);			if(!n2.hidden()) {				g.drawLine(xpix(n1), ypix(n1),xpix(n2), ypix(n2));				n1 = n2;			}		}		//g.drawString(" x:" + Double.toString(xval(x)) + " y:" + Integer.toString(y), 10, 10);	//	g.drawString(" x:" + Integer.toString(x) + " y:" + Integer.toString(y), 10, 30);				g.setColor(new Color(0, 0, 0, 50));		int llp = xpix(this.getModel().getLoopLength());		if(llp < this.getWidth()) {			g.clearRect(llp, 0, this.getWidth()-llp, 				xpix(this.getModel().getMaxVal()- this.getModel().getMinVal()));		}			}		/*	*	draws a dot at the specified coordinates	*/	private void drawDot(int x, int y, Graphics g, int r) {		g.drawOval(x-r, y-r, 2*r, 2*r);	}		/*	*	the code that is triggered when the mouse is dragged	*/	private void drag() {		if( xpix(selectedNode) >    xpix(adj[1])) {  //if the curently selected node crosses another node to the right					if(adj[1].hidden()) {				adj[1].show();		// show the node if it is hidden				} else {				adj[1].hide();		// hide the node that is being passed by			}			overNode = selectedNode;  // acts as a switch of sorts (if a different one is selected, it deletes						adj[0] = adj[1];	// as the selected node passes by, the new adj[0] will be the old adj[1]			adj[1] = nodeVector.next(pos + 1); //the new adj[1] will be the next one along			nodeVector.swapNext(pos);	// makes the swap			pos++;		}				if( xpix(selectedNode) <  xpix(adj[0])) { //if the curently selected node crosses another node to the right				if(adj[0].hidden()) { // show the node if it is hidden					adj[0].show();				} else {				adj[0].hide();		// hides the node that is being passed by			}						overNode = selectedNode;  						//general swapping maneuvers			adj[1] = adj[0];			adj[0] = nodeVector.prev(pos - 1);			nodeVector.swapPrev(selectedNode, pos);			pos--;		}		repaint();	} 	 	/** 	*	 	*/	public void	mousePressed(MouseEvent e) {				int [] l = new int [1];		Node closest = nodeVector.closest(xval(e.getX()), l);		// if it is within the radius of the circle		if((xpix(closest)+r) > e.getX() && (xpix(closest)-r)<e.getX()) {			selectedNode = closest;			pos = l[0];			setInBounds(e, selectedNode);		} else {			Node n = new Node();			setInBounds(e, n);						pos = nodeVector.addNode(n);   //returns the position that is now occupies			selectedNode = n;		}				adj = nodeVector.getAdjacent(pos);		repaint();	}			/** this method is used to update the selected node in a way that ensures 	*	that it is within the bounds	*/	private void setInBounds(MouseEvent e, Node n) {				if(n == null) return;		int y = e.getY();		if(y <= 0 ) {			n.setYPos(nodeVector.getMaxVal());		} else if(y >= this.getSize().height) {			n.setYPos(nodeVector.getMinVal());		} else {			n.setYPos(((this.getSize().height-y)*1.0/this.getSize().height)*nodeVector.getDiff() + 					nodeVector.getMinVal());		}				int x = e.getX();		//int loopix = xpix(this.getModel().getLoopLength());		//PO.p("loopix = " + loopix);		if(x <= this.stWin) {			n.setXPos(this.stWin);		} else if( xval(x) >=  this.getModel().getLoopLength()) {			n.setXPos(this.getModel().getLoopLength());		} else {			n.setXPos(xval(x));		//n.setXPos((x*1.0/loopix*1.0)*(loopix - this.stWin) + this.stWin);		}	}		private double xval(int x) {		// pixels * pixels per beat = beat;		return x*1.0/ppb.getValue()*1.0;//(x*1.0/this.dr.width*1.0)*(this.enWin - this.stWin) + this.stWin;	}		private int xpix(Node n) {		return xpix(n.getXPos());	}		private int ypix(Node n) {		return ypix(n.getYPos());	}		private int xpix(double at) {		return (int)(at*ppb.getValue());//(int)(at*(this.dr.width/(this.enWin-this.stWin)));	}		private int ypix(double val) {		return (int)(this.getSize().height - // invert height						(this.getSize().height*( // height * percent value:								(val-nodeVector.getMinVal())/   // value								(nodeVector.getMaxVal()-nodeVector.getMinVal()) // div. range							)						)				);	}		/**	*	If a node has been selected when the mouse button was pressed, this method will	*	update the position of the node, so that it is equal to the position of the mouse	*	*	care must be also taken so that if the mouse is released outside of the bounds of the	*	graph, the node only goes as far as the edge.	*/	public void	mouseDragged(MouseEvent e) {		setInBounds(e, selectedNode);		drag();			}		/*	*		*/	public void	mouseReleased(MouseEvent e) {		pos -= nodeVector.deleteHidden(pos);			repaint();	}			/**	*	the only thing this will do is print out the current position of the mouse	*/	public void	mouseMoved(MouseEvent e) {		x = e.getX();		y = e.getY();		repaint();	}			public void mouseClicked(MouseEvent e)  {	}		public void	mouseEntered(MouseEvent e) {	}	 	public void	mouseExited(MouseEvent e) { 	}	public ParameterMap getZoom() {		return this.ppb;	}}