package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.wrappers.PlayerItemLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LifeInsurance extends RenownUpgrade {
	public static LifeInsurance INSTANCE;

	public LifeInsurance() {
		super("Uber Insurance", "LIFE_INSURANCE", 75, 22, 12, true, 3);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isApplicable(killEvent.getDeadPlayer())) return;
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : killEvent.getVulnerableItems().entrySet()) {
			KillEvent.ItemInfo itemInfo = entry.getValue();
			if(!itemInfo.pitItem.isMystic) continue;
			killEvent.removeVulnerableItem(entry.getKey());
		}
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this) && UpgradeManager.getTier(player, this) > 0)
			lore.add(ChatColor.translateAlternateColorCodes('&',
					"&7Current: &eTier " + AUtil.toRoman(UpgradeManager.getTier(player, this))));
		if(UpgradeManager.hasUpgrade(player, this) && UpgradeManager.getTier(player, this) > 0)
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each Tier:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Retain mystic lives on death when on"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier I: &f500 &dUber"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier II: &f450 &dUber"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier III: &f400 &dUber"));

		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	public static boolean isApplicable(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		int tier = UpgradeManager.getTier(player, INSTANCE.refName);

		if(!(pitPlayer.megastreak instanceof Uberstreak)) return false;
		if(pitPlayer.getKills() >= 400 && tier >= 3) return true;
		if(pitPlayer.getKills() >= 450 && tier >= 2) return true;
		if(pitPlayer.getKills() >= 500 && tier >= 1) return true;
		return false;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(40, 75, 150);
	}
}
