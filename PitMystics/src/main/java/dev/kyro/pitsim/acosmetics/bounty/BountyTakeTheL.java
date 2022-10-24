package dev.kyro.pitsim.acosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyTakeTheL extends PitCosmetic {

	public BountyTakeTheL() {
		super("&5&lTake The &6&lL", "takethel", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return deadName + "&7 took the L to " + killerName + "&7 and had to hand over " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.SIGN)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Quite unfortunate... for them"
				))
				.getItemStack();
		return itemStack;
	}
}
