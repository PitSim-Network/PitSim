package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.TieredRenownUpgrade;
import net.pitsim.spigot.enchants.overworld.Billionaire;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TaxEvasion extends TieredRenownUpgrade {
	public static TaxEvasion INSTANCE;

	public TaxEvasion() {
		super("Tax Evasion", "TAX_EVASION", 22);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.IRON_FENCE)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&6-" + (tier * 5) + "% gold";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Hits with " + Billionaire.INSTANCE.getDisplayName(false, true) +
				" &7cost &6-5% less";
	}

	@Override
	public String getSummary() {
		return "&eTax Evasion&7 is a &erenown&7 upgrade that makes the enchant &dRARE! &9Billionaire&7 cost less &6gold";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(25, 50, 75);
	}
}
