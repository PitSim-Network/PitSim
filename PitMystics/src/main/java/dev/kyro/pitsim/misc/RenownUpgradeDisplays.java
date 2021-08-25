package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.enums.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenownUpgradeDisplays {

	public static ItemStack getDisplayItem(RenownUpgrade upgrade, Player player) {
		if(upgrade.equals(RenownUpgrade.GOLD_BOOST)) {
			ItemStack item = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "Renown Gold Boost");
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(renownCostString(upgrade, player));
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
		if(upgrade.equals(RenownUpgrade.XP_BOOST)) {
			ItemStack item = new ItemStack(Material.EXP_BOTTLE);
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(renownCostString(upgrade, player));
			meta.setLore(lore);
			meta.setDisplayName(ChatColor.AQUA + "Renown XP Boost");
			item.setItemMeta(meta);
			return item;
		}
		if(upgrade.equals(RenownUpgrade.TENACITY)) {
			ItemStack item = new ItemStack(Material.MAGMA_CREAM);
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(renownCostString(upgrade, player));
			meta.setLore(lore);
			meta.setDisplayName(ChatColor.RED + "Tenacity");
			item.setItemMeta(meta);
			return item;
		}

		return null;
	}

	private static String renownCostString(RenownUpgrade upgrade, Player player) {
		if(!upgrade.isTiered) {
			if(!RenownUpgrade.hasUpgrade(player, upgrade)) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
			return ChatColor.GREEN + "Already unlocked!";
		}
		if(RenownUpgrade.getTier(player, upgrade) == 0) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
		if(RenownUpgrade.getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Fully upgraded!";
		return ChatColor.YELLOW + upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade)).toString() + " Renown";
	}

	public static List<Integer> goldBoostCosts = Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	public static List<Integer> XPBoostCosts = Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	public static List<Integer> TenacityCosts = Arrays.asList(10, 50);
}
