package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager {

	public static List<RenownUpgrade> upgrades = new ArrayList<>();

	public static void registerUpgrade(RenownUpgrade upgrade) {

		upgrades.add(upgrade);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(upgrade, PitSim.INSTANCE);
	}

	public static boolean hasUpgrade(Player player, RenownUpgrade upgrade) {
		if(NonManager.getNon(player) != null) return false;
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		return playerData.contains(upgrade.refName);
//        return playerData.contains(upgrade.name()) && playerData.getBoolean(upgrade.name());

	}

	public static boolean hasUpgrade(Player player, String upgrade) {
		if(NonManager.getNon(player) != null) return false;
		for(RenownUpgrade renownUpgrade : UpgradeManager.upgrades) {
			if(renownUpgrade.refName.equals(upgrade)) {
				FileConfiguration playerData = APlayerData.getPlayerData(player);
				return playerData.contains(renownUpgrade.refName);
			}
		}
		return false;
	}

	public static int getTier(Player player, RenownUpgrade upgrade) {
		if(NonManager.getNon(player) != null) return 0;
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		if(!playerData.contains(upgrade.refName)) return 0;
		else return playerData.getInt(upgrade.refName);
	}

	public static int getTier(Player player, String upgrade) {
		if(NonManager.getNon(player) != null) return 0;
		for(RenownUpgrade renownUpgrade : UpgradeManager.upgrades) {
			if(renownUpgrade.refName.equals(upgrade)) {
				FileConfiguration playerData = APlayerData.getPlayerData(player);
				if(!playerData.contains(renownUpgrade.refName)) return 0;
				else return playerData.getInt(renownUpgrade.refName);
			}
		}
		return 0;
	}


	public static String renownCostString(RenownUpgrade upgrade, Player player) {
		if(!upgrade.isTiered) {
			if(!hasUpgrade(player, upgrade)) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
			return ChatColor.GREEN + "Already unlocked!";
		}
		if(getTier(player, upgrade) == 0) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
		if(getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Fully upgraded!";
		return ChatColor.YELLOW + upgrade.getTierCosts().get(getTier(player, upgrade)).toString() + " Renown";
	}

	public static String itemNameString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && hasUpgrade(player, upgrade)) return ChatColor.GREEN + upgrade.name;
		if(upgrade.isTiered && getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + upgrade.name;
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + upgrade.name;
		if(upgrade.isTiered && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade))) return ChatColor.RED + upgrade.name;
		return ChatColor.YELLOW + upgrade.name;
	}

	public static String purchaseMessageString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && hasUpgrade(player, upgrade)) return ChatColor.GREEN + "Unlocked!";
		if(upgrade.isTiered && getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Max tier unlocked!";
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + "Not enough renown!";
		if(upgrade.isTiered && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade))) return ChatColor.RED + "Not enough renown!";
		return ChatColor.YELLOW + "Click to purchase!";
	}

	public static List<String> loreBuilder(RenownUpgrade upgrade, Player player, List<String> originalLore, boolean isCustomPanel) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		List<String> lore = new ArrayList<>(originalLore);
		lore.add("");
		if(!isCustomPanel && upgrade.getCustomPanel() != null && hasUpgrade(player, upgrade)) {
			lore.add(ChatColor.YELLOW + "Click to open menu!");
			return lore;
		}
		if(upgrade.isTiered && getTier(player, upgrade) != upgrade.maxTiers) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.getTierCosts().get(getTier(player, upgrade)) + " Renown"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
			lore.add("");
		}
		if(!upgrade.isTiered && !hasUpgrade(player, upgrade)) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.renownCost + " Renown"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
			lore.add("");
		}
		lore.add(purchaseMessageString(upgrade, player));

		return lore;
	}
}

