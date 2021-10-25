package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
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

public class Tenacity extends RenownUpgrade {
	public Tenacity() {
		super("Tenacity", "TENACITY", 10, 11, 1, true, 2);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: Heal &c" + 0.5 * UpgradeManager.getTier(player, this) + "\u2764 &7on kill."));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each tier:");
		lore.add(ChatColor.GRAY + "Heal " + ChatColor.RED + "0.5\u2764 "  + ChatColor.GRAY + "on kill.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 50);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.killer, this)) return;

		int tier = UpgradeManager.getTier(killEvent.killer, this);
		if(tier == 0) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		pitKiller.heal(tier);
	}
}
