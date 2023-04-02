package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class UberIncrease extends TieredRenownUpgrade {
	public static UberIncrease INSTANCE;

	public UberIncrease() {
		super("Venture Capitalist", "UBER_INCREASE", 25);
		INSTANCE = this;
	}

	public static int getUberIncrease(Player player) {
		return UpgradeManager.getTier(player, UberIncrease.INSTANCE);
	}

	@Override
	public ItemStack getBaseItemStack() {
		return new AItemStackBuilder(Material.WATCH)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&d+" + tier + " Uberstreak" + Misc.s(tier);
	}

	@Override
	public String getEffectPerTier() {
		return "&7Daily &dUberstreak &7limit is increased by &d1";
	}

	@Override
	public String getSummary() {
		return "&dVenture Capitalist&7 is an &erenown&7 upgrade that increases your daily &dUberstreak&7 limit";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(25, 50, 75, 100, 125);
	}
}
