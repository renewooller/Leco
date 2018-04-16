package com.lemu.leco;
/**
 * LeCo - LeMorpheus, composer edition
 * 
 * TODO:
 * log4j / aspects
 * 
 * 
 * by Rene Wooller
 *  
 */

import java.io.IOException;

import com.lemu.leco.gui.LeCoFrame;

public class LeCo {

	
	
	public static void main(String[] args) {
		try {
			new LeCo(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LeCo(String [] args) throws IOException {
		System.setProperty("com.apple.macos.smallTabs", "true");
		
		LeCoFrame fr = new LeCoFrame();
		
		int limit = Integer.MAX_VALUE;
		
		for(int i=0 ; i< args.length; i++) {	
			if(args[i].startsWith("-n")) {
				limit = Integer.parseInt(args[i].split("n")[1]);
			} else {
				fr.loadMidi(System.getProperty("user.dir") + 
					System.getProperty("file.separator") + args[i], limit);
			}
		}
		
	}

}