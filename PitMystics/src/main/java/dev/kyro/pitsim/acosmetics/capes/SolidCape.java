package dev.kyro.pitsim.acosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.ParticleCollection;
import dev.kyro.pitsim.acosmetics.ParticleOffset;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.acosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SolidCape extends PitCosmetic {
	public ParticleCollection particleCollection = new ParticleCollection();

	public SolidCape() {
		super("&fSolid Cape", "solidcape", CosmeticType.CAPE);
		accountForPitch = false;
		isColorCosmetic = true;

		RedstoneColor redstoneColor = RedstoneColor.RED;
		RedstoneParticle particle = new RedstoneParticle(this, redstoneColor);
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 7; j++) {
				particleCollection.addParticle("main", particle,
						new ParticleOffset(-0.225 + 0.15 * i, -0.15 * j, -0.25 - 0.04 * j));
			}
		}
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Location displayLocation = pitPlayer.player.getLocation();
				displayLocation.add(0, 1.4, 0);

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					particleCollection.display(entityPlayer, displayLocation);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		runnable.cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
