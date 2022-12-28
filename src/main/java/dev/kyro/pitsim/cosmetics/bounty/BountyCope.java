package dev.kyro.pitsim.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyCope extends PitCosmetic {

	public BountyCope() {
		super("&2Cope", "cope", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 made " + deadName + "&7 cope for " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.POTION)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7It's only a game. Why do",
						"&7they get so mad?"
				))
				.getItemStack();
		return itemStack;
	}
}
