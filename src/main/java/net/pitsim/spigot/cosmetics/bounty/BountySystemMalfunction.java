package net.pitsim.spigot.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountySystemMalfunction extends PitCosmetic {

	public BountySystemMalfunction() {
		super("&a&lS&2&ly&a&ls&2&lt&a&le&2&lm &4&lM&8&la&c&ll&7&lf&4&lu&8&ln&c&lc&7&lt&4&li&8&lo&c&ln&7&l!",
				"systemmalfunction", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return deadName + ".exe&7 has stopped responding. " + killerName + "&7 killed the process and obtained " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.COMMAND)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Playere.exe has stopped responding"
				))
				.getItemStack();
		return itemStack;
	}
}
