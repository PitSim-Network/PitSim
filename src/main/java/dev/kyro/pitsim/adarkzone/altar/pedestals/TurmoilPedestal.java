package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TurmoilPedestal extends AltarPedestal {
	public TurmoilPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&2&lTURMOIL";
	}

	@Override
	public int getActivationCost() {
		return 300;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.SAPLING, 1, 3)
				.setName("&2Pedestal of Turmoil")
				.setLore(new ALoreBuilder(
						"&7This pedestal severely &2randomizes",
						"&7your reward chances.",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
