package net.pitsim.spigot.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyHunted extends PitCosmetic {

	public BountyHunted() {
		super("&6Hunter", "hunter", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 hunted " + deadName + "&7 for " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_LEGGINGS)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Become the best bounty",
						"&7hunter in the west!"
				))
				.getItemStack();
		return itemStack;
	}
}
