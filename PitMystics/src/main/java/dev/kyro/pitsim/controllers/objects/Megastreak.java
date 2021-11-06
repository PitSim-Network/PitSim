package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Megastreak implements Listener {

	public PitPlayer pitPlayer;

	public Megastreak(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getName();
	public abstract String getRawName();
	public abstract String getPrefix();
	public abstract List<String> getRefNames();
	public abstract int getRequiredKills();
	public abstract ItemStack guiItem();
	public abstract int guiSlot();
	public abstract int prestigeReq();
	public abstract int levelReq();

	public abstract void stop();
	public abstract void proc();
	public abstract void reset();

	public void kill() {}

	public boolean isOnMega() {
		return pitPlayer.getKills() >= getRequiredKills();
	}

	public boolean playerIsOnMega(KillEvent killEvent) {
		return PitPlayer.getPitPlayer(killEvent.killer).getKills() >= getRequiredKills();
	}


}
