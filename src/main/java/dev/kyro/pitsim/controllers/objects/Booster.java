package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.FirestoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Booster implements Listener {
	public String name;
	public String refName;
	public int minutes;
	public int slot;
	public ChatColor color;

	public Booster(String name, String refName, int slot, ChatColor color) {
		this.name = name;
		this.refName = refName;
		this.slot = slot;
		this.color = color;
		this.minutes = FirestoreManager.CONFIG.boosters.getOrDefault(refName, 0);
	}

	public abstract List<String> getDescription();

	public abstract ItemStack getDisplayItem();

	public void disable() {
		minutes = 0;
		updateTime();
		onDisable();
	}

	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER! &7" + color + name + "&7 no longer active"));
	}

	public boolean isActive() {
		return minutes > 0;
	}

	public void updateTime() {
		FirestoreManager.CONFIG.boosters.put(refName, minutes);
	}


	public static int getBoosterAmount(Player player, Booster booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.boosters.getOrDefault(booster.refName, 0);
	}

	public static int getBoosterAmount(Player player, String booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				return pitPlayer.boosters.getOrDefault(booster1.refName, 0);
			}
		}
		return 0;
	}

	public static Booster getBooster(String booster) {
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				return booster1;
			}
		}
		return null;
	}

	public static void setBooster(Player player, Booster booster, int amount) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.boosters.put(booster.refName, amount);
//		saveBoosters(player);
	}

	public static void setBooster(Player player, String booster, int amount) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				pitPlayer.boosters.put(booster1.refName, amount);
			}
		}
//		saveBoosters(player);
	}

//	public static void saveBoosters(Player player) {
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		for(Map.Entry<String, Integer> boosterIntegerEntry : pitPlayer.boosters.entrySet()) {
//			playerData.set("boosters." + boosterIntegerEntry.getKey(), boosterIntegerEntry.getValue());
//		}
//	}
}
