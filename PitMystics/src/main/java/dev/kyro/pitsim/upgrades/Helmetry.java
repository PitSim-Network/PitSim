package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.inventories.RenownShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Helmetry extends RenownUpgrade {
	public Helmetry() {
		super("Helmetry", "HELMETRY", 25, 23, 15, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.GOLD_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Unlock the ability to craft"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&6Golden Helmets &7for &e10"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eRenown &7each. &6Golden Helmets"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7can be upgraded by putting"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&6gold &7into them."));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return RenownShopGUI.helmetryPanel;}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}
}
