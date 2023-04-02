package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager implements Listener {
	public static List<RenownUpgrade> upgrades = new ArrayList<>();

	public static void registerUpgrade(RenownUpgrade upgrade) {
		int index = upgrades.size();
		for(int i = 0; i < upgrades.size(); i++) {
			RenownUpgrade testUpgrade = upgrades.get(i);
			if(upgrade.prestigeReq >= testUpgrade.prestigeReq) continue;
			index = i;
			break;
		}
		upgrades.add(index, upgrade);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(upgrade, PitSim.INSTANCE);
	}

	public static ChatColor getChatColor(Player player, RenownUpgrade upgrade) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		ChatColor chatColor = ChatColor.YELLOW;
		if(isMaxed(player, upgrade)) {
			chatColor = ChatColor.GREEN;
		} else if(pitPlayer.renown < getNextCost(player, upgrade)) {
			chatColor = ChatColor.RED;
		}
		return chatColor;
	}

	public static int getNextCost(Player player, RenownUpgrade upgrade) {
		if(upgrade.isTiered()) {
			return upgrade.getTierCosts().get(getTier(player, upgrade));
		} else {
			return upgrade.getUnlockCost();
		}
	}

	public static boolean hasUpgrade(Player player, PitPerk pitPerk) {
		RenownUpgrade upgrade = getUpgrade(pitPerk.upgradeRef);
		return hasUpgrade(player, upgrade);
	}

	public static boolean hasUpgrade(Player player, RenownUpgrade upgrade) {
		if(!PlayerManager.isRealPlayer(player) || upgrade == null) return false;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(upgrade.prestigeReq > pitPlayer.prestige) return false;

		return pitPlayer.renownUpgrades.getOrDefault(upgrade.refName, 0) > 0;
	}

	public static boolean isMaxed(Player player, RenownUpgrade upgrade) {
		if(!PlayerManager.isRealPlayer(player) || upgrade == null) return false;
		if(!(upgrade instanceof TieredRenownUpgrade)) return hasUpgrade(player, upgrade);
		TieredRenownUpgrade tieredUpgrade = (TieredRenownUpgrade) upgrade;
		return getTier(player, upgrade) == tieredUpgrade.getMaxTiers();
	}

	public static int getTier(Player player, RenownUpgrade upgrade) {
		if(!PlayerManager.isRealPlayer(player) || upgrade == null) return 0;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(upgrade.prestigeReq > pitPlayer.prestige) return 0;

		return pitPlayer.renownUpgrades.getOrDefault(upgrade.refName, 0);
	}

	public static RenownUpgrade getUpgrade(String refName) {
		for(RenownUpgrade upgrade : upgrades) {
			if(!upgrade.refName.equalsIgnoreCase(refName)) continue;
			return upgrade;
		}
		return null;
	}

//	public static String itemNameString(RenownUpgrade upgrade, Player player) {
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		if(!upgrade.isTiered() && hasUpgrade(player, upgrade)) return ChatColor.GREEN + upgrade.name;
//		if(upgrade.isTiered() && getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + upgrade.name;
//		if(!upgrade.isTiered() && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + upgrade.name;
//		if(upgrade.isTiered() && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade)))
//			return ChatColor.RED + upgrade.name;
//		return ChatColor.YELLOW + upgrade.name;
//	}
//
//	public static String purchaseMessageString(RenownUpgrade upgrade, Player player) {
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		if(!upgrade.isTiered() && hasUpgrade(player, upgrade)) return ChatColor.GREEN + "Unlocked!";
//		if(upgrade.isTiered() && getTier(player, upgrade) == upgrade.maxTiers)
//			return ChatColor.GREEN + "Max tier unlocked!";
//		if(!upgrade.isTiered() && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + "Not enough renown!";
//		if(upgrade.isTiered() && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade)))
//			return ChatColor.RED + "Not enough renown!";
//		return ChatColor.YELLOW + "Click to purchase!";
//	}
//
//	public static List<String> loreBuilder(RenownUpgrade upgrade, Player player, List<String> originalLore, boolean isCustomPanel) {
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		List<String> lore = new ArrayList<>(originalLore);
//		lore.add("");
//		if(isCustomPanel && hasUpgrade(player, upgrade)) {
//			lore.add(ChatColor.YELLOW + "Click to open menu!");
//			return lore;
//		}
//		if(upgrade.isTiered() && getTier(player, upgrade) != upgrade.maxTiers) {
//			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.getTierCosts().get(getTier(player, upgrade)) + " Renown"));
//			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
//			lore.add("");
//		}
//		if(!upgrade.isTiered() && !hasUpgrade(player, upgrade)) {
//			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.renownCost + " Renown"));
//			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
//			lore.add("");
//		}
//		lore.add(purchaseMessageString(upgrade, player));
//
//		return lore;
//	}
}

