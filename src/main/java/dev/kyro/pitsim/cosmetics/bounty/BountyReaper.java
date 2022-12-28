package dev.kyro.pitsim.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyReaper extends PitCosmetic {

	public BountyReaper() {
		super("&8&lGrim Reaper", "reaper", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 harvested " + deadName + "'s&7 soul for " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.IRON_HOE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Now I am become Death, the",
						"&7the destroyer of worlds"
				))
				.getItemStack();
		return itemStack;
	}
}
