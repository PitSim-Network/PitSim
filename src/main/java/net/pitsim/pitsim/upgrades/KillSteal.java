package net.pitsim.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.controllers.objects.TieredRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class KillSteal extends TieredRenownUpgrade {
	public static KillSteal INSTANCE;

	public KillSteal() {
		super("Kill Steal", "KILL_STEAL", 27);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.SHEARS)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&e+" + (tier * 10) + "% &7on &aassists";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain &e+10% &7on your &aassists&7. 100% &aassists &7are converted into &ckills";
	}

	@Override
	public String getSummary() {
		return "&eKill Steal&7 is a &erenown&7 upgrade that increases the percentage on all your assists " +
				"(and turns 100% assists into kills)";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(20, 30, 40);
	}
}
