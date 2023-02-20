package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.particles.ExplosionLargeParticle;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RuptureAbility extends RoutinePitBossAbility {
	public double damage;

	public RuptureAbility(double routineWeight, double damage) {
		super(routineWeight);
		this.damage = damage;
	}

	@Override
	public void onRoutineExecute() {

		Location firstLocation = pitBoss.boss.getLocation().add(5, 0, 0);
		new GravitizedBlock(getClosestViewer(firstLocation), firstLocation);

		Location secondLocation = pitBoss.boss.getLocation().add(-5, 0, -1);

		Location thirdLocation = pitBoss.boss.getLocation().add(0, 0, -2);

		new BukkitRunnable() {
			@Override
			public void run() {
				new GravitizedBlock(getClosestViewer(secondLocation), secondLocation);
			}
		}.runTaskLater(PitSim.INSTANCE, 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				new GravitizedBlock(getClosestViewer(thirdLocation), thirdLocation);
			}
		}.runTaskLater(PitSim.INSTANCE, 10);
	}

	public class GravitizedBlock {
		public Player viewer;
		public Location initialLocation;
		public double vectorLength;
		public int ticks = 0;

		public GravitizedBlock(Player viewer, Location initialLocation) {
			this.viewer = viewer;
			this.initialLocation = initialLocation;

			spawnBlock();
		}

		public void spawnBlock() {
			FallingBlock fallingBlock = new FallingBlock(Material.CACTUS, (byte) 0, initialLocation);
			fallingBlock.setViewers(getViewers());
			fallingBlock.spawnBlock();

//			initialLocation.add(0, 2, 0);

			new BukkitRunnable() {
				@Override
				public void run() {
					fallingBlock.setVelocity(new Vector(0, 0.7, 0));
				}
			}.runTaskLater(PitSim.INSTANCE, 1);


			new BukkitRunnable() {
				@Override
				public void run() {
					int blockTicks = ticks;
					fallingBlock.setVelocity(new Vector(0, 0.1, 0));

					ticks++;
					if(blockTicks >= 20) this.cancel();
				}
			}.runTaskTimer(PitSim.INSTANCE, 10, 1);

			new BukkitRunnable() {
				@Override
				public void run() {
					initialLocation.add(0, 5.5, 0);
					Vector vector = viewer.getLocation().toVector().subtract(initialLocation.toVector());
					vectorLength = vector.length();
					vector.multiply(0.25);
					fallingBlock.setVelocity(vector);


					new BukkitRunnable() {
						@Override
						public void run() {
							viewer.damage(damage, pitBoss.boss);
							Sounds.CREEPER_EXPLODE.play(viewer.getLocation(), 10);

							for(Player player : getViewers()) {
								EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
								new ExplosionLargeParticle().display(entityPlayer, viewer.getLocation().subtract(0, 0.5, 0));

								fallingBlock.removeAfter(5);
							}
						}
					}.runTaskLater(PitSim.INSTANCE, (long) (vectorLength * 0.25));

				}
			}.runTaskLater(PitSim.INSTANCE, 31);
		}

	}

	public List<Player> getViewers() {
		List<Player> viewers = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(50, 50, 50)) {
			if(!(entity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(entity.getUniqueId());
			if(player != null) viewers.add(player);
		}
		return viewers;
	}

	public Player getClosestViewer(Location location) {
		Player nearest = null;
		double distance = 0;
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 20, 20, 20)) {
			if(!(nearbyEntity instanceof Player)) continue;
			if(pitBoss.boss == nearbyEntity) continue;

			double entityDistance = nearbyEntity.getLocation().distance(location);
			if(nearest == null || distance > entityDistance) {
				nearest = (Player) nearbyEntity;
				distance = entityDistance;
			}
		}
		return nearest;
	}
}

