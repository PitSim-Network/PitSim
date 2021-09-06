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

public class UnlockFirstStrike extends RenownUpgrade {
	public UnlockFirstStrike() {
		super("Perk unlock: First Strike", "FIRST_STRIKE", 15, 20, 10, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.COOKED_CHICKEN);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Required level: " + ChatColor.YELLOW + this.levelReq);
		lore.add("");
		lore.add(ChatColor.YELLOW + "First Strike");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7First hit on a player deals"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c+35% damage &7and grants"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eSpeed I &7(5s)"));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}
}
