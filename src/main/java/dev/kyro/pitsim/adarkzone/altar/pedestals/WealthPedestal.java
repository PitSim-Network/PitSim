package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.misc.Misc;
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
