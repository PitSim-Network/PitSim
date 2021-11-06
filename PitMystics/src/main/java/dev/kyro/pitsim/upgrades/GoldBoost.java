package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoldBoost extends RenownUpgrade {

	public GoldBoost() {
		super("Renown Gold Boost", "GOLD_BOOST", 10, 10, 1, true, 10);
	}
	List<Integer> goldBoostCosts = Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
			ItemStack item = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(UpgradeManager.itemNameString(this, player));
			List<String> lore = new ArrayList<>();
			if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
					"&7Current: &6+" + 2.5 * UpgradeManager.getTier(player, this) + "&6% gold (g)"));
			if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
			if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
			lore.add(ChatColor.GRAY + "Each tier:");
			lore.add(ChatColor.GRAY + "Earn " + ChatColor.GOLD + "+2.5% gold (g) " + ChatColor.GRAY + "from");
			lore.add(ChatColor.GRAY + "kills.");
			meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
			item.setItemMeta(meta);
			return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.killer, this)) return;

		int tier = UpgradeManager.getTier(killEvent.killer, this);
		if(tier == 0) return;

		double percent = 2.5 * tier;

		killEvent.goldMultipliers.add((percent / 100D) + 1);
	}
}
