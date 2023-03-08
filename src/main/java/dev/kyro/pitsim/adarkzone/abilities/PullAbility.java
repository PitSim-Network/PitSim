package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PullAbility extends RoutinePitBossAbility {
	public double maxRadius;
	public double interval;
	public MaterialData materialData;

	public PullAbility(double routineWeight, double maxRadius, double interval, MaterialData materialData) {
		super(routineWeight);

		this.maxRadius = maxRadius;
		this.interval = interval;
		this.materialData = materialData;
	}

	@Override
	public void onRoutineExecute() {
		Location location = pitBoss.boss.getLocation().subtract(0, 3, 0);
		BlockCrackParticle particle = new BlockCrackParticle(materialData);
		List<Player> affectedPlayers = new ArrayList<>();

		Sounds.PULL.play(pitBoss.boss.getLocation(), 40);

		for(int radius = 0; radius < maxRadius; radius += interval) {
			int finalRadius = radius;

			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player viewer : getViewers()) {
						if(viewer.getLocation().distance(location) > finalRadius) continue;

						if(affectedPlayers.contains(viewer)) continue;

						affectedPlayers.add(viewer);
						Vector distanceVector = location.subtract(viewer.getLocation()).toVector().setY(0);
						double distance = Math.min(distanceVector.length(), maxRadius);
						Vector horizontalVelocity = distanceVector.clone().normalize().multiply(distance * 0.16);
						double yComponent = Math.min(distance * 0.02 + 0.23, 0.65);
						Vector finalVelocity = horizontalVelocity.clone().setY(yComponent);

						viewer.setVelocity(finalVelocity);
					}

					for(int degrees = 0; degrees < 360; degrees += 5) {
						double x = Math.cos(Math.toRadians(degrees)) * finalRadius;
						double z = Math.sin(Math.toRadians(degrees)) * finalRadius;
						Location particleLocation = location.clone().add(x, 0, z);

						for(int i = 0; i < 5; i++) {
							if(particleLocation.getBlock().getType() == Material.AIR) {
								break;
							}

							particleLocation.setY(particleLocation.getY() + 1);
						}

						for(Player viewer : getViewers()) {
							EntityPlayer nmPlayer = ((CraftPlayer) viewer).getHandle();
							particle.display(nmPlayer, particleLocation.clone().add(x, 0.4, z));
						}
					}
				}
			}.runTaskLater(PitSim.INSTANCE, radius * 2L);
		}
	}

	@Override
	public boolean shouldExecuteRoutine() {
		for(Player viewer : getViewers()) {
			if(viewer.getLocation().distance(pitBoss.boss.getLocation()) < 6) return false;
		}
		return true;
	}
}
