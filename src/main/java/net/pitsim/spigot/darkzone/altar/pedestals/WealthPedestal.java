package net.pitsim.spigot.darkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.DarkzoneBalancing;
import net.pitsim.spigot.darkzone.altar.AltarPedestal;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class WealthPedestal extends AltarPedestal {
	public WealthPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&6&lWEALTH";
	}

	@Override
	public int getActivationCost() {
		return 500;
	}

	@Override
	public ParticleColor getParticleColor() {
		return ParticleColor.GOLD;
	}

	@Override
	public ItemStack getItem(Player player) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_NUGGET)
				.setName("&6Pedestal of Wealth")
				.setLore(new ALoreBuilder(
						"&7This pedestal increases rewards",
						"&7from other reward categories &6+" + decimalFormat.format(DarkzoneBalancing.PEDESTAL_WEALTH_MULTIPLIER) + "%",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
