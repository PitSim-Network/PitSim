package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.PerkManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Killstreak {

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
	public abstract ItemStack getDisplayItem();

	public static Killstreak getKillstreak(String refName) {
		for(Killstreak killstreak : PerkManager.killstreaks) {
			if(killstreak.refName.equals(refName)) return killstreak;
		}
		return null;
	}
}
