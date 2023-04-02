package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.objects.UnlockableRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UnlockStreaker extends UnlockableRenownUpgrade {
	public static UnlockStreaker INSTANCE;

	public UnlockStreaker() {
		super("Perk Unlock: Streaker", "STREAKER", 7);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseItemStack() {
		return new AItemStackBuilder(Material.WHEAT)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Upon reaching your &emegastreak&7, gain &bmore XP &7the faster you hit mega. Passively gain &b+80 max XP";
	}

	@Override
	public String getSummary() {
		return "&eStreaker &7is a perk unlocked in the &erenown shop&7 that gives you a higher &bXP cap &7and more " +
				"&bXP &7based on how quickly you activate a &cMegastreak";
	}

	@Override
	public int getUnlockCost() {
		return 30;
	}
}
