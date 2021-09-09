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

public class UnlockStreaker extends RenownUpgrade {
	public UnlockStreaker() {
		super("Perk unlock: Streaker", "STREAKER", 30, 28, 20, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
			ItemStack item = new ItemStack(Material.WHEAT);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(UpgradeManager.itemNameString(this, player));
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Required level: " + ChatColor.YELLOW + this.levelReq);
			lore.add("");
			lore.add(ChatColor.YELLOW + "Streaker");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Upon reaching your &emegastreak&7,"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7gain &b+100 max XP &7if it took &f30 &7or"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7less seconds. Subtracts &b10 max"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&bXP &7per additional &f10 &7seconds."));
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
