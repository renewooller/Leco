
package ren.music.Player;

import jm.midi.MidiSynth;
import jm.music.data.Score;
import ren.gui.seqEdit.BaseBeatTracker;

public interface Playable {

    public void go();
    public void stop();
    public void pause();
    public boolean isPlaying();
    public void setScore(Score s);
    public void addPrePostPlayerEventListener(PrePostPlayerEventListener pregoeli);
    public void setBeatTracker(BaseBeatTracker bt);
    public BaseBeatTracker getBeatTracker();
}
