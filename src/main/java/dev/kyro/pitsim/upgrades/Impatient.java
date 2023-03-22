package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Impatient extends RenownUpgrade {
	public static RenownUpgrade INSTANCE;

	public Impatient() {
		super("Impatient", "IMPATIENT", 10, 14, 6, true, 2);
		INSTANCE = this;
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.CARROT_ITEM);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this) && UpgradeManager.getTier(player, this) > 0)
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.YELLOW + "Tier I: " + ChatColor.GRAY + "Gain Speed II in spawn area.");
		lore.add(ChatColor.YELLOW + "Tier II: " + ChatColor.GRAY + "Gain Speed IV in Darkzone spawn.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 25);
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(UpgradeManager.hasUpgrade(player, INSTANCE) && SpawnManager.isInSpawn(player)) continue;
					if(MapManager.inDarkzone(player)) {
						Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40, 3, false, false);
					} {
						Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40, 1, false, false);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public String getSummary() {
		return "&aImpatient &7is a &erenown &7upgrade that grant you &espeed&7 in spawn";
	}
}
