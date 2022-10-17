package dev.kyro.pitsim.acosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
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

public class SolidCape extends ColorableCosmetic {
	public ParticleCollection standingCape = new ParticleCollection();
	public ParticleCollection sneakingCape = new ParticleCollection();

	public SolidCape() {
		super("&fSolid Cape", "solidcape", CosmeticType.CAPE);
		accountForPitch = false;
		isColorCosmetic = true;
		isPermissionRequired = true;

		RedstoneParticle particle = new RedstoneParticle(this);
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 7; j++) {
				standingCape.addParticle("main", particle,
						new ParticleOffset(-0.225 + 0.15 * i, -0.15 * j, -0.25 - 0.04 * j));
				sneakingCape.addParticle("main", particle,
						new ParticleOffset(-0.225 + 0.15 * i, -0.15 * j, -0.25 - 0.08 * j));
			}
		}
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if(!CosmeticManager.isStandingStill(pitPlayer.player)) return;

				Location displayLocation = pitPlayer.player.getLocation();
				double increase = 1.4;
				if(pitPlayer.player.isSneaking()) increase -= 0.25;
				displayLocation.add(0, increase, 0);

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					if(pitPlayer.player.isSneaking()) {
						sneakingCape.display(entityPlayer, displayLocation, getRedstoneColor(pitPlayer.player));
					} else {
						standingCape.display(entityPlayer, displayLocation, getRedstoneColor(pitPlayer.player));
					}
				}
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
