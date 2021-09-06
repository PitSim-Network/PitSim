package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shardhunter extends RenownUpgrade {
	public Shardhunter() {
		super("Shardhunter", "SHARDHUNTER", 40, 34, 400, true, 10);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.EMERALD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &f" + 0.01 * UpgradeManager.getTier(player, this) + " &f% &7drop chance"));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gain &f+0.01% &7chance to obtain a &aGem"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&aShard &7on kill. &7Use &aGem Shards"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7to create &aTotally Legit Gems&7."));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(40, 45, 50 , 55, 60, 70, 80, 90, 100, 120);
	}
}
