package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.PitSim;
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
import java.util.List;

public class Impatient extends RenownUpgrade {
	public static RenownUpgrade INSTANCE;
	public Impatient() {
		super("Impatient", "IMPATIENT", 10, 14, 6, false, 0);
		INSTANCE = this;
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.CARROT_ITEM);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Gain Speed II in spawn area.");
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

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(UpgradeManager.hasUpgrade(player, INSTANCE) && SpawnManager.isInSpawn(player.getLocation())) {
						Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40, 1, false, false);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}
}
