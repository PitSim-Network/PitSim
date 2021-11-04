package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.AConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Booster implements Listener {
	public String name;
	public String refName;
	public int minutes;

	public Booster(String name, String refName) {
		this.name = name;
		this.refName = refName;
		minutes = Math.max(AConfig.getInt("boosters." + refName), 3);
	}

	public abstract List<String> getDescription();
	public abstract ItemStack getDisplayItem();

	public void disable() {
		minutes = 0;
		updateTime();
		onDisable();
	}

	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &7" + name + "&7 no longer active"));
	}

	public boolean isActive() {
		return minutes > 0;
	}

	public void updateTime() {

		AConfig.set("booster." + refName, minutes);
	}
}
