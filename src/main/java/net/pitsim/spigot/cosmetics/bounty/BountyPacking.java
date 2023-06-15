package net.pitsim.spigot.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyPacking extends PitCosmetic {

	public BountyPacking() {
		super("&7Packing", "packing", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return deadName + "&7 was sent packing by " + killerName + "&7 and left behind " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.CHEST)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Send your foes packing!"
				))
				.getItemStack();
		return itemStack;
	}
}
