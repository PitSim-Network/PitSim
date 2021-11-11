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
import java.util.List;

public class UnlockCounterJanitor extends RenownUpgrade {
	public UnlockCounterJanitor() {
		super("Perk unlock: Counter-Janitor", "COUNTER_JANITOR", 20, 20, 14, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.SPONGE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Required prestige: " + ChatColor.YELLOW + AUtil.toRoman(this.prestigeReq));
		lore.add("");
		lore.add(ChatColor.YELLOW + "Counter-Janitor");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Instantly heal half your"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&chealth &7on player kill."));
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
