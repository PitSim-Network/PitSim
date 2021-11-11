package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ReportAccess extends RenownUpgrade {
	public ReportAccess() {
		super("Access to /report", "REPORT_ACCESS", 10, 21, 11, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.SIGN);
		ItemMeta meta = item.getItemMeta();
//		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7You now have access to a premium"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7report system to get cheaters"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7banned faster!"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(Removable if abused)"));
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
