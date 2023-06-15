package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FastPass extends UnlockableRenownUpgrade {
	public static FastPass INSTANCE;

	public FastPass() {
		super("Fast Pass", "FAST_PASS", 38);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.ACTIVATOR_RAIL)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Start at level 50 after you &eprestige";
	}

	@Override
	public String getSummary() {
		return "&eFast Pass&7 is a &erenown&7 upgrade that increases the level you start on after completing a &eprestige";
	}

	@Override
	public int getUnlockCost() {
		return 100;
	}
}
