package net.pitsim.spigot.darkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.altar.AltarPedestal;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.misc.Misc;
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
		return "&4&lHERESY";
	}

	@Override
	public int getActivationCost() {
		return 100;
	}

	@Override
	public ParticleColor getParticleColor() {
		return ParticleColor.DARK_RED;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.EMPTY_MAP)
				.setName("&4Pedestal of Heresy")
				.setLore(new ALoreBuilder(
						"&7This pedestal increases your",
						"&7chance of gaining &4Demonic Vouchers",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
