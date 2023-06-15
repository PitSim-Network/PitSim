package net.pitsim.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.controllers.objects.UnlockableRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UnlockCounterJanitor extends UnlockableRenownUpgrade {
	public static UnlockCounterJanitor INSTANCE;

	public UnlockCounterJanitor() {
		super("Perk Unlock: Counter-Janitor", "COUNTER_JANITOR", 14);
		INSTANCE = this;
	}

	@Override
	public String getSummary() {
		return "&eCounter-Janitor is a perk unlocked in the &erenown shop&7 that &cheals you&7 for substantially on " +
				"player kill. This perk is incompatible with &cVampire";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.SPONGE)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Instantly heal half your &chealth &7on player kill";
	}

	@Override
	public int getUnlockCost() {
		return 20;
	}
}
