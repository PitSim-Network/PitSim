package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.ahelp.Summarizable;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Killstreak implements Listener, Summarizable {
	public String displayName;
	public String refName;
	public int killInterval;
	public int prestigeReq;

	public Killstreak(String displayName, String refName, int killInterval, int prestigeReq) {
		this.displayName = displayName;
		this.killInterval = killInterval;
		this.refName = refName;
		this.prestigeReq = prestigeReq;
	}

	public abstract void proc(Player player);
	public abstract void reset(Player player);
	public abstract ItemStack getDisplayStack(Player player);

	public static Killstreak getKillstreak(String refName) {
		if(refName == null) return NoKillstreak.INSTANCE;
		for(Killstreak killstreak : PerkManager.killstreaks) {
			if(killstreak.refName.equals(refName)) return killstreak;
		}
		return NoKillstreak.INSTANCE;
	}

	public static boolean hasKillstreak(Player player, Killstreak killstreak) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Killstreak killstreaks : pitPlayer.killstreaks) {
			if(killstreaks.refName.equals(killstreak.refName)) return true;
		}
		return false;
	}

	public static boolean hasKillstreak(Player player, String killstreak) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Killstreak killstreaks : pitPlayer.killstreaks) {
			if(killstreaks.refName.equals(killstreak)) return true;
		}
		return false;
	}

	@Override
	public String getIdentifier() {
		return "KILLSTREAK_" + refName.toUpperCase().replaceAll("[- ]", "_");
	}

	@Override
	public List<String> getTrainingPhrases() {
		List<String> trainingPhrases = new ArrayList<>();
		trainingPhrases.add("what is " + ChatColor.stripColor(displayName) + "?");
		trainingPhrases.add("what does " + ChatColor.stripColor(displayName) + " do?");
		return trainingPhrases;
	}
}
