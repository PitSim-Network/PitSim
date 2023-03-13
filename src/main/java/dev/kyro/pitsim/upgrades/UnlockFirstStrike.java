package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
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
		super("Perk unlock: First Strike", "FIRST_STRIKE", 25, 19, 10, false, 0);
	}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.COOKED_CHICKEN);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Required prestige: " + ChatColor.YELLOW + AUtil.toRoman(this.prestigeReq));
		lore.add("");
		lore.add(ChatColor.YELLOW + "First Strike");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7First hit on a player deals"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c+30% damage &7and grants"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eSpeed I &7(5s)"));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&eFirst Strike &7is a perk unlocked in the &erenown shop&7 that increases your &cdamage&7 and gives " +
				"you &fSpeed&7 on your first hit against bots and players";
	}
}
