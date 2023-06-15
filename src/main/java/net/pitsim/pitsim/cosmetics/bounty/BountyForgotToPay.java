package net.pitsim.pitsim.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyForgotToPay extends PitCosmetic {

	public BountyForgotToPay() {
		super("&c&lForgot To &a&lPay", "forgottopay", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return deadName + "&7 forgot to pay " + killerName + "&7 " + bounty + "&7 for truce";
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.EMERALD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Their truce payment has run",
						"&7out. Time to end their streaks!"
				))
				.getItemStack();
		return itemStack;
	}
}
