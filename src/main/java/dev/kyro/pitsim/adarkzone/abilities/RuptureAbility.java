package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.cosmetics.particles.ExplosionLargeParticle;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuptureAbility extends PitBossAbility {
	public double damage;
	public int projectileCount;
	public int radius;

	public RuptureAbility(double routineWeight, int projectileCount, double damage, int radius) {
		super(routineWeight);
		this.damage = damage;
		this.projectileCount = projectileCount;
		this.radius = radius;
	}

	@Override
	public void onRoutineExecute() {
		Location centerLocation = getPitBoss().boss.getLocation().clone().subtract(0, 1, 0);
		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;

				if(blockLocation.getBlock().getType() != Material.AIR && blockLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
					applicableBlocks.add(blockLocation.getBlock());
					continue;
				}

				for(int i = -2; i < 3; i++) {
					Location checkPosition = blockLocation.clone().add(0, i, 0);
					if(checkPosition.getBlock().getType() == Material.AIR || checkPosition.clone().add(0, 1, 0).getBlock().getType() != Material.AIR)
						continue;
					applicableBlocks.add(checkPosition.getBlock());
				}
			}
		}

		List<Block> usedBlocks = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < projectileCount; i++) {
			Block randomBlock = applicableBlocks.get(random.nextInt(applicableBlocks.size()));
			if(usedBlocks.contains(randomBlock)) {
				i--;
				continue;
			}

			usedBlocks.add(randomBlock);

			Player viewer = getClosestViewer(randomBlock.getLocation());
			if(viewer == null) continue;

			new BukkitRunnable() {
				@Override
				public void run() {
					if(!isEnabled() || !isNearToBoss(viewer)) return;
					new GravitizedBlock(viewer, randomBlock);
				}
			}.runTaskLater(PitSim.INSTANCE, i * 2L);
		}
	}

	public class GravitizedBlock {
		public Player viewer;
		public Block block;
		public Location initialLocation;
		public double vectorLength;
		public int ticks = 0;

		public GravitizedBlock(Player viewer, Block block) {
			this.viewer = viewer;
			this.block = block;
			this.initialLocation = block.getLocation().clone();

			spawnBlock();
		}

		public void spawnBlock() {
			FallingBlock fallingBlock = new FallingBlock(block.getType(), block.getData(), initialLocation.add(0, 1, 0));
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
							DamageManager.createDirectAttack(getPitBoss().boss, viewer, damage);
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

	public Player getClosestViewer(Location location) {
		Player nearest = null;
		double distance = 0;
		for(Entity nearbyEntity : location.getWorld().getNearbyEntities(location, 50, 50, 50)) {
			if(!(nearbyEntity instanceof Player)) continue;
			if(!Misc.isValidMobPlayerTarget(nearbyEntity, getPitBoss().boss)) continue;

			double entityDistance = nearbyEntity.getLocation().distance(location);
			if(nearest == null || distance > entityDistance) {
				nearest = (Player) nearbyEntity;
				distance = entityDistance;
			}
		}
		return nearest;
	}
}

