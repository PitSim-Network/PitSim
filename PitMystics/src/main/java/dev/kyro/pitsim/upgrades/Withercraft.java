package dev.kyro.pitsim.upgrades;

import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Withercraft extends RenownUpgrade {
	public Withercraft() {
		super("Withercraft", "WITHERCRAFT", 50, 31, 30, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Ability to right-click a");
		lore.add(ChatColor.DARK_PURPLE + "Chunk of Vile " + ChatColor.GRAY + "and sacrifice");
		lore.add(ChatColor.GRAY + "it to repair a life on a ");
		lore.add(ChatColor.DARK_AQUA + "Jewel " + ChatColor.GRAY + "item.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}
}
