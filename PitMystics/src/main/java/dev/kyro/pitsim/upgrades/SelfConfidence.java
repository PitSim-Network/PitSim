package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SelfConfidence extends RenownUpgrade {
	public SelfConfidence() {
		super("Self Confidence", "SELF_CONFIDENCE", 35, 29, 23, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.PAINTING);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Earn bonus &eRenown&7:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Top 1: &e+2 Renown"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Top 3: &e+1 Renown"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7in major events when 10"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7or more players are online."));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}
}
