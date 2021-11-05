package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.controllers.BoosterManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public abstract class Booster implements Listener {
	public String name;
	public String refName;
	public int minutes;
	public int slot;

	public Booster(String name, String refName) {
		this.name = name;
		this.refName = refName;
//		this.slot = slot;
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


	public static int getBoosterAmount(Player player, Booster booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.boosters.get(booster);
	}

	public static int getBoosterAmount(Player player, String booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				return pitPlayer.boosters.get(booster1);
			}
		}
		return 0;
	}
	public static void setBooster(Player player, Booster booster, int amount)  {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.boosters.put(booster, amount);
		saveBoosters(player);
	}

	public static void setBooster(Player player, String booster, int amount)  {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				pitPlayer.boosters.put(booster1, amount);
			}
		}
		saveBoosters(player);
	}

	public static void saveBoosters(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		for(Map.Entry<Booster, Integer> boosterIntegerEntry : pitPlayer.boosters.entrySet()) {
			playerData.set("boosters." + boosterIntegerEntry.getKey().refName, boosterIntegerEntry.getValue());
		}
		APlayerData.savePlayerData(player);
	}
}
