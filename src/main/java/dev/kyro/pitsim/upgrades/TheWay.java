package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TheWay extends TieredRenownUpgrade {
	public static TheWay INSTANCE;

	public TheWay() {
		super("The Way", "THE_WAY", 33);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.ACACIA_DOOR_ITEM)
				.getItemStack();
	}

	@Override
	public String getEffectPerTier() {
		return "&7Lower level requirements by &e5[]levels";
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&e-" + (tier * 5) + " levels";
	}

	@Override
	public String getSummary() {
		return "&eThe Way&7 is a &erenown&7 upgrade that decreases level requirement for " +
				"&cMegastreaks&7, &aKillstreakers&7, and &6Trading";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(50, 100, 150, 200, 250, 300, 350, 400, 450, 500);
	}

	public int getLevelReduction(Player player) {
		return UpgradeManager.getTier(player, this) * 5;
	}
}
