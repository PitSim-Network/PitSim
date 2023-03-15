package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DivineIntervention extends RenownUpgrade {
	public static DivineIntervention INSTANCE;

	public DivineIntervention() {
		super("Divine Intervention", "DIVINE_INTERVENTION", 25, 24, 16, true, 5);
		INSTANCE = this;
	}

	public static boolean attemptDivine(Player player) {
		if(!UpgradeManager.hasUpgrade(player, INSTANCE)) return false;

		int tier = UpgradeManager.getTier(player, INSTANCE);
		if(tier == 0) return false;

		double chance = 0.01 * (tier * 5);

		boolean isDouble = Math.random() < chance;

		if(isDouble) {
			AOutput.send(player, "&b&lDIVINE INTERVENTION!&7 Inventory saved!");

			Sounds.SoundMoment soundMoment = new Sounds.SoundMoment(3);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.5);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.6);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.7);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.7);
			soundMoment.play(player);
		}

		return isDouble;
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(25, 50, 75, 100, 125);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.QUARTZ_STAIRS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &e" + (5 * UpgradeManager.getTier(player, this)) + "% chance"));
		if(UpgradeManager.hasUpgrade(player, this))
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each Tier:");
		lore.add(ChatColor.YELLOW + "+5% chance " + ChatColor.GRAY + "to keep your");
		lore.add(ChatColor.GRAY + "inventory on death.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&eDivine Intervention&7 is an &erenown&7 upgrade  gives you a small chance to save your inventory on death";
	}
}
