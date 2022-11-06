package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager implements Listener {
//	public static Map<UUID, Map<RenownUpgrade, Integer>> upgradeMap = new HashMap<>();
	public static List<RenownUpgrade> upgrades = new ArrayList<>();

//	public static void updatePlayer(Player player) {
//		APlayer aPlayer = APlayerData.getPlayerData(player);
//		Map<RenownUpgrade, Integer> playerMap = new HashMap<>();
//		for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
//			if(aPlayer.playerData.contains(upgrade.refName))
//				playerMap.put(upgrade, aPlayer.playerData.getInt(upgrade.refName));
//		}
//		upgradeMap.put(player.getUniqueId(), playerMap);
//	}

	public static void registerUpgrade(RenownUpgrade upgrade) {
		upgrades.add(upgrade);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(upgrade, PitSim.INSTANCE);
	}

	public static boolean hasUpgrade(Player player, RenownUpgrade upgrade) {
		if(NonManager.getNon(player) != null) return false;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		return pitPlayer.upgrades.getOrDefault(upgrade.refName, 0) > 0;
	}

	public static boolean hasUpgrade(Player player, String refName) {
		if(NonManager.getNon(player) != null) return false;
		RenownUpgrade upgrade = getUpgrade(refName);
		if(upgrade == null) return false;
		return hasUpgrade(player, upgrade);
	}

	public static int getTier(Player player, RenownUpgrade upgrade) {
		if(NonManager.getNon(player) != null) return 0;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		return pitPlayer.upgrades.get(upgrade.refName);
	}

	public static int getTier(Player player, String refName) {
		if(NonManager.getNon(player) != null || PitBoss.isPitBoss(player)) return 0;
		RenownUpgrade upgrade = getUpgrade(refName);
		return getTier(player, upgrade);
	}

	public static RenownUpgrade getUpgrade(String refName) {
		for(RenownUpgrade upgrade : upgrades) {
			if(!upgrade.refName.equalsIgnoreCase(refName)) continue;
			return upgrade;
		}
		return null;
	}

//	public static String renownCostString(RenownUpgrade upgrade, Player player) {
//		if(!upgrade.isTiered) {
//			if(!hasUpgrade(player, upgrade)) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
//			return ChatColor.GREEN + "Already unlocked!";
//		}
//		if(getTier(player, upgrade) == 0) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
//		if(getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Fully upgraded!";
//		return ChatColor.YELLOW + upgrade.getTierCosts().get(getTier(player, upgrade)).toString() + " Renown";
//	}

	public static String itemNameString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && hasUpgrade(player, upgrade)) return ChatColor.GREEN + upgrade.name;
		if(upgrade.isTiered && getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + upgrade.name;
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + upgrade.name;
		if(upgrade.isTiered && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade)))
			return ChatColor.RED + upgrade.name;
		return ChatColor.YELLOW + upgrade.name;
	}

	public static String purchaseMessageString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && hasUpgrade(player, upgrade)) return ChatColor.GREEN + "Unlocked!";
		if(upgrade.isTiered && getTier(player, upgrade) == upgrade.maxTiers)
			return ChatColor.GREEN + "Max tier unlocked!";
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + "Not enough renown!";
		if(upgrade.isTiered && pitPlayer.renown < upgrade.getTierCosts().get(getTier(player, upgrade)))
			return ChatColor.RED + "Not enough renown!";
		return ChatColor.YELLOW + "Click to purchase!";
	}

	public static List<String> loreBuilder(RenownUpgrade upgrade, Player player, List<String> originalLore, boolean isCustomPanel) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		List<String> lore = new ArrayList<>(originalLore);
		lore.add("");
		if(!isCustomPanel && hasUpgrade(player, upgrade)) {
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

