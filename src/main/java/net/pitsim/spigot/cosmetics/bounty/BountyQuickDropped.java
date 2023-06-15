package net.pitsim.spigot.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyQuickDropped extends PitCosmetic {

	public BountyQuickDropped() {
		super("&aQuick &eDropped", "quickdrop", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 quick-dropped " + deadName + "&7 for " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.DROPPER)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7You're now quick-dropping your",
						"&7enemies for a quick buck!"
				))
				.getItemStack();
		return itemStack;
	}
}
