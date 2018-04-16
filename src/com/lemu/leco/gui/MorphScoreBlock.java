package com.lemu.leco.gui;

import java.awt.Color;

import com.lemu.music.MultiMorphPreRenderer;

public class MorphScoreBlock extends ScoreBlock {

	protected Color borderColor = Color.MAGENTA;
	
	protected MultiMorphPreRenderer morph = new MultiMorphPreRenderer();
	
	public MorphScoreBlock() {
		super();
	}
	
	public MorphScoreBlock construct(ArrangePanel arrPan) {
		return (MorphScoreBlock)super.construct(arrPan);
	}
	
	
}
