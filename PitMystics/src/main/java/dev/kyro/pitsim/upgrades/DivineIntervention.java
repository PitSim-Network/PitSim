package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DivineIntervention extends RenownUpgrade {
	public DivineIntervention() {
		super("Divine Intervention", "DIVINE_INTERVENTION", 25, 30, 25, true, 3);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.QUARTZ_STAIRS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &e" + (5 * UpgradeManager.getTier(player, this)) + "% chance"));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.YELLOW + "+5% chance " + ChatColor.GRAY + "to keep your");
		lore.add(ChatColor.GRAY + "inventory on death.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(25, 50, 75);
	}
}
