package dev.kyro.pitsim.controllers.killstreaks;

import dev.kyro.pitsim.controllers.PitPlayer;

import java.util.List;

public abstract class Megastreak {

	public PitPlayer pitPlayer;

	public abstract String getName();
	public abstract String getPrefix();
	public abstract List<String> getRefNames();
	public abstract int getRequiredKills();

	public Megastreak(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
	}

	public abstract void proc();
	public abstract void reset();

	public void kill() {}

	public boolean isOnMega() {
		return pitPlayer.getKills() >= getRequiredKills();
	}
}
