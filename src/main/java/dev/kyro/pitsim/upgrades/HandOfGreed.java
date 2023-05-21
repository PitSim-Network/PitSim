package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.megastreaks.Prosperity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class HandOfGreed extends TieredRenownUpgrade {
	public static HandOfGreed INSTANCE;

	public HandOfGreed() {
		super("Hand of Greed", "HAND_OF_GREED", 35);
		INSTANCE = this;
	}

	public static int getGoldIncrease(Player player) {
		return getGoldIncrease(UpgradeManager.getTier(player, INSTANCE));
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.BOWL)
				.getItemStack();
	}

	@Override
	public String getEffectPerTier() {
		return "&7Earn &6EXACTLY +50 gold &7from kills while on " + Prosperity.INSTANCE.getCapsDisplayName() + " &7(ignores buffs and cap)";
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&6EXACTLY +" + getGoldIncrease(tier) + " gold";
	}

	@Override
	public String getSummary() {
		return "&eThe Way&7 is a &erenown&7 upgrade that decreases level requirement for " +
				"&cMegastreaks&7, &aKillstreakers&7, and &6Trading";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(50, 60, 65, 70, 75, 80, 85, 90, 95, 100);
	}

	public static int getGoldIncrease(int tier) {
		return tier * 50;
	}
}
