package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.PerkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Killstreak implements Listener {

	public String name;
	public String refName;
	public int killInterval;
	public int prestige;
	public Killstreak INSTANCE;

	public Killstreak(String name, String refName, int killInterval, int prestige) {
		this.name = name;
		this.killInterval = killInterval;
		this.refName = refName;
		this.prestige = prestige;
	}

	public abstract void proc(Player player);
	public abstract void reset(Player player);
	public abstract ItemStack getDisplayItem(Player player);

	public static Killstreak getKillstreak(String refName) {
		for(Killstreak killstreak : PerkManager.killstreaks) {
			if(killstreak.refName.equals(refName)) return killstreak;
		}
		return null;
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
}
