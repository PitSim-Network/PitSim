package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeresyPedestal extends AltarPedestal {
	public HeresyPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&5&lHERESY";
	}

	@Override
	public int getActivationCost() {
		return 0;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.EMPTY_MAP)
				.setName("&5Pedestal of Heresy")
				.setLore(new ALoreBuilder(
						"&7This pedestal increases your",
						"&7chance of gaining &4Demonic Vouchers&7.",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
