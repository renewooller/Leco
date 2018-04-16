package ren.music.Player;

public interface PrePostPlayerEventListener {

	public void preGoEventTriggered(Playable player);
	public void postStopEventTriggered();
}
