package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KnowledgePedestal extends AltarPedestal {
	public KnowledgePedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&3&lKNOWLEDGE";
	}

	@Override
	public int getActivationCost() {
		return 100;
	}

	@Override
	public ParticleColor getParticleColor() {
		return ParticleColor.DARK_AQUA;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.EXP_BOTTLE)
				.setName("&3Pedestal of Knowledge")
				.setLore(new ALoreBuilder(
						"&7This pedestal increases your",
						"&7chance of gaining &cAltar XP",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
		));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
