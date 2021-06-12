package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class Megastreak implements Listener {

	public PitPlayer pitPlayer;

	public Megastreak(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getName();
	public abstract String getPrefix();
	public abstract List<String> getRefNames();
	public abstract int getRequiredKills();

	public abstract void proc();
	public abstract void reset();

	public void kill() {}

	public boolean isOnMega() {
		return pitPlayer.getKills() >= getRequiredKills();
	}
}
