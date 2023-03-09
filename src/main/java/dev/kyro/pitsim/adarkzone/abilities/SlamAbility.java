package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.PitParticle;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.cosmetics.particles.ExplosionHugeParticle;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import dev.kyro.pitsim.misc.effects.PacketBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlamAbility extends PitBossAbility {
	public int radius;
	public int blockCount;
	public double damage;

	PitParticle dirt = new BlockCrackParticle(new MaterialData(Material.DIRT));

	public SlamAbility(double routineWeight, int radius, int blockCount, double damage) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
		this.damage = damage;
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

		List<Location> usedLocations = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < blockCount; i++) {
			int index = random.nextInt(applicableBlocks.size());
			Location spawnLocation = applicableBlocks.get(index).getLocation();
			if(usedLocations.contains(spawnLocation)) {
				i--;
				continue;
			}
			usedLocations.add(spawnLocation);
		}

		for(int i = 0; i < usedLocations.size(); i++) {
			Block block = usedLocations.get(i).getBlock();
			int delay = i * 2;
			int baseTime = blockCount > 20 ? 40 + blockCount : 40;

			new BukkitRunnable() {
				@Override
				public void run() {
					GravitizedBlock gravitizedBlock = new GravitizedBlock(block);
					gravitizedBlock.slamAfter(baseTime - delay);

				}
			}.runTaskLater(PitSim.INSTANCE, delay);


		}
	}

	public class GravitizedBlock {
		public Block block;
		public Location initialLocation;
		public FallingBlock fallingBlock;
		public PacketBlock packetBlock;
		
		public BukkitTask runnable;

		public GravitizedBlock(Block block) {
			this.block = block;
			this.initialLocation = block.getLocation();

			packetBlock = new PacketBlock(Material.BARRIER, (byte) 0, initialLocation)
					.setViewers(getViewers())
					.spawnBlock();

			initialLocation.add(0.5, 1, 0.5);
			spawnBlock();
		}

		public void spawnBlock() {
			fallingBlock = new FallingBlock(block.getType(), block.getData(), initialLocation);
			fallingBlock.setViewers(getViewers());
			fallingBlock.spawnBlock();

			new BukkitRunnable() {
				@Override
				public void run() {
					fallingBlock.setVelocity(new Vector(0, 0.7, 0));
					Sounds.SLAM.play(initialLocation, 20);

					for(Player viewer : getViewers()) {

						EntityPlayer entityPlayer = ((CraftPlayer) viewer).getHandle();

						for(int j = 0; j < 25; j++) {
							dirt.display(entityPlayer, initialLocation, new ParticleOffset(0, 1, 0, 1, 1, 1));
						}
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 1);


			runnable = new BukkitRunnable() {
				@Override
				public void run() {
					fallingBlock.setVelocity(new Vector(0, 0.075, 0));
				}
			}.runTaskTimer(PitSim.INSTANCE, 10, 1);
		}

		public void slamAfter(int ticks) {
			new BukkitRunnable() {
				@Override
				public void run() {
					slam();
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);
		}

		public void slam() {
			runnable.cancel();
			Vector vector = new Vector(0, -1.5, 0);
			fallingBlock.setVelocity(vector);
			fallingBlock.removeAfter(3);
			packetBlock.removeAfter(3);

			for(int i = 0; i < 10; i++) {
				Location location = initialLocation.clone().add(0, -i, 0);
				if(location.getBlock().getType() == Material.AIR) continue;

				for(Player viewer : getViewers()) {
					EntityPlayer entityPlayer = ((CraftPlayer) viewer).getHandle();
					new ExplosionHugeParticle().display(entityPlayer, location.add(0, 0.5, 0));
					for(int j = 0; j < 25; j++) {
						dirt.display(entityPlayer, initialLocation, new ParticleOffset(0, 0, 0, 1, 1, 1));
					}

					Sounds.SLAM_2.play(initialLocation, 20);

					Location viewerLocation = viewer.getLocation();
					double distance = viewerLocation.distance(initialLocation);

					if(distance > 5) continue;

					double multiplier = Math.pow(5 - distance, 1.5);
					Vector playerVector = viewerLocation.toVector().subtract(initialLocation.toVector());
					playerVector.add(new Vector(0, 0.1, 0));
					playerVector.normalize();
					playerVector.multiply(0.3);
					playerVector.multiply(multiplier);

					viewer.setVelocity(playerVector);
					DamageManager.createAttack(getPitBoss().boss, viewer, damage);
				}
				break;
			}
		}
	}
}