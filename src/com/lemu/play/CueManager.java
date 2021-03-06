/*
 * Created on Jun 3, 2006
 *
 * @author Rene Wooller
 */
package com.lemu.play;

import com.lemu.music.MusicGenerator;

public interface CueManager {

	public void mgCue(MusicGenerator mg);
	public void removeCue();
	public LPlayer getLPlayer();
	public void trackerEndCue(MusicGenerator current, MusicGenerator next);
}
