package ren.gui.seqEdit;

import java.util.ArrayList;

import ren.gui.ParameterMap;
import ren.util.RMath;

public class BaseBeatTracker {

	private static double update_resolution_beats = Double.valueOf(System
			.getProperty("com.lemu.update_resolution_beats", "0.25"));
	private ParameterMap scope;
	private double res;
	private int bv;
	private ArrayList<BeatListener> beatlis_usrMod = new ArrayList<BeatListener>(16);
	private ArrayList<BeatListener> beatlis_auto = new ArrayList<BeatListener>(16);
	/**
	 * Increments by one resolution, and returns the resolution step that it was
	 * previously at
	 */
	double toRet;
	private boolean userMod = false;
	private int stbv = -1;

	public BaseBeatTracker() {
		super();
	}

	public BaseBeatTracker contruct() {
		return construct(new ParameterMap().construct(4, 16, 8, "scope"));
	}

	public BaseBeatTracker construct(ParameterMap scope) {
		return construct(scope, update_resolution_beats);
	}

	public BaseBeatTracker construct(ParameterMap scope, double res) {
		if (scope != null)
			this.scope = scope;
		this.res = res;
		
		return (BaseBeatTracker)this;
	}

	public void setBeat(double beat) {
	//	System.out.println("BBT setting beat : " + beat);
		beat = RMath.absMod(beat, scope.getValue());
	    
		//set it
		bv = (int) (beat / res);
	
	    fireBeatListeners(bv*res);
	
	}

	public double at() {
	    return bv * res;
	}
	
	public double next() {
	    
		return ((bv + 1) % (int) (scope.getValue() / res)) * res;
	}

	public double prev() {
	    
		return RMath.absMod((bv - 1), (int) (scope.getValue() / res)) * res;
	}

	public double nextNoMod() {
	    
		return (bv + 1) * res;
	}

	public double increment() {
	    	
		toRet = bv * res;
		fireBeatListeners(toRet);
	
		bv++;
		bv = bv % (int) (scope.getValue() / res);
		return toRet;
	}

	public double decrement() {
	    	    
		toRet = bv * res;
		bv--;
		bv = RMath.absMod(bv, (int) (scope.getValue() / res));
		return toRet;
	}

	public void setScope(ParameterMap scope) {
		this.scope = scope;
	}

	/**
	 * this method is deprecated... useing setLPlayer now
	 * to support automatic resolution changes
	 * @param res
	 */
	public void setRes(double res) {
		this.res = res;
	}

	public void addUserModBeatListener(BeatListener toAdd) {
		beatlis_usrMod.add(toAdd);
	}
	
	public void addBeatListenerAuto(BeatListener toAdd) {
		beatlis_auto.add(toAdd);
	}

	public void fireUserModBeatListeners(double beat) {
		for(BeatListener b : beatlis_usrMod) {
			b.beatFired(beat);
		}
	}
	
	public void fireBeatListeners(double beat) {
		for(BeatListener b : beatlis_auto) {
			b.beatFired(beat);
		}
	}

	public void userMod(boolean b) {
	    this.userMod = b;
	    if(b)
	        stbv = this.bv;
	     else
	        stbv = -1;
	}

	public void userModBeat(double beat) {
	    
	    beat = RMath.absMod(beat, scope.getValue());
	
	    //set it
	    int nbv = (int) (beat / res);
	    
	    if(nbv != bv) {
	    	bv = nbv;
	    	Thread   thread =   new Thread() {
	    		public void run() {
	    			fireUserModBeatListeners(bv*res);
	    		}
	    	};
	    	thread.start();
	    	
	    }
	}

	public ParameterMap getScope() {	
		return this.scope;
	}
	
	/**
	 * beat increment resolution
	 * @return
	 */
	public double getRes() {
		return res;	
	}

}