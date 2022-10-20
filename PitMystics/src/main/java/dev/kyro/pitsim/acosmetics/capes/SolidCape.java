package dev.kyro.pitsim.acosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.ParticleColor;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.ColorableCosmetic;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.collections.CapeCollection;
import dev.kyro.pitsim.acosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SolidCape extends ColorableCosmetic {
	public CapeCollection cape;

	public SolidCape() {
		super("&fSolid Cape", "solidcape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new RedstoneParticle(this));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		ParticleColor particleColor = getColor(pitPlayer);
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(SolidCape.this, pitPlayer, particleColor);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
