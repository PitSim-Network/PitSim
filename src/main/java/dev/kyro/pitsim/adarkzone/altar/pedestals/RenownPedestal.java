package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RenownPedestal extends AltarPedestal {
	public RenownPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&e&lRENOWN";
	}

	@Override
	public int getActivationCost() {
		return 0;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.QUARTZ)
				.setName("&ePedestal of Renown")
				.setLore(new ALoreBuilder(
						"&7This pedestal increases your",
						"&7chance of gaining &eRenown&7.",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
