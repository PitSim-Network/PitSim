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

public class LifeInsurance extends RenownUpgrade {
	public LifeInsurance() {
		super("Life Insurance", "LIFE_INSURANCE", 75, 22, 12, true, 3);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &eTier " + AUtil.toRoman(UpgradeManager.getTier(player, this))));
		if(UpgradeManager.hasUpgrade(player, this))
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Retain mystic lives on death when on"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier I: &f500 &cOvrdrv&7, &dUber"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier II: &f1,000 &aBeast&7, &6High&7, &bMoon"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier III: &f4,000 &5RNG"));

		//TODO Add functionality and decide on final values for Life Insurance
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {
		return null;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(40, 75, 150);
	}
}
