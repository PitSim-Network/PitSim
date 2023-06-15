package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UnlockFirstStrike extends UnlockableRenownUpgrade {
	public static UnlockFirstStrike INSTANCE;

	public UnlockFirstStrike() {
		super("Perk Unlock: First Strike", "FIRST_STRIKE", 10);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.COOKED_CHICKEN)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7First hit on a player deals &c+30% damage";
	}

	@Override
	public int getUnlockCost() {
		return 25;
	}

	@Override
	public String getSummary() {
		return "&eFirst Strike &7is a perk unlocked in the &erenown shop&7 that increases your &cdamage&7 and gives " +
				"you &eSpeed&7 on your first hit against bots and players";
	}
}
