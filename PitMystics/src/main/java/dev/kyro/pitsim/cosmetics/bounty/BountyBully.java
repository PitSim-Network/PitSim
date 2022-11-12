package dev.kyro.pitsim.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyBully extends PitCosmetic {

	public BountyBully() {
		super("&5Bully", "bully", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 bullied " + deadName + "&7 into giving them " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.OBSIDIAN)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Become the biggest bully on",
						"&7the block by claiming bounties",
						"&7left and right!"
				))
				.getItemStack();
		return itemStack;
	}
}
