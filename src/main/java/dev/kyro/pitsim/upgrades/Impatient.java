package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Impatient extends TieredRenownUpgrade {
	public static Impatient INSTANCE;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!UpgradeManager.hasUpgrade(player, INSTANCE) || !SpawnManager.isInSpawn(player)) continue;
					Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40,
							UpgradeManager.getTier(player, INSTANCE) - 1, false, false);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public Impatient() {
		super("Impatient", "IMPATIENT", 6);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseItemStack() {
		return new AItemStackBuilder(Material.CARROT_ITEM)
				.getItemStack();
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain an additional level of &eSpeed &7in spawn";
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&eSpeed " + AUtil.toRoman(tier) + " &7while in spawn";
	}

	@Override
	public String getSummary() {
		return "&aImpatient &7is a &erenown &7upgrade that grant you &eSpeed&7 in spawn";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 25, 40);
	}
}
