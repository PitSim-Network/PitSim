package net.pitsim.spigot.darkzone.abilities;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.PitBossAbility;
import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.cosmetics.particles.RedstoneParticle;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.effects.FallingBlock;
import net.pitsim.spigot.misc.effects.PacketBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollapseAbility extends PitBossAbility {
	public int radius;
	public int patchCount;
	public int roomRadius;
	public int warningTime;
	public double damage;

	public CollapseAbility(double routineWeight, int radius, int patchCount, int warningTime, double damage) {
		super(routineWeight);
		this.radius = radius;
		this.patchCount = patchCount;
		this.roomRadius = 25;
		this.warningTime = warningTime;
		this.damage = damage;
	}

	@Override
	public void onRoutineExecute() {
		Location centerLocation = getPitBoss().getSubLevel().getMiddle().clone();
		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -1 * roomRadius; x < roomRadius + 1; x++) {
			for(int z = -1 * roomRadius; z < roomRadius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > roomRadius) continue;

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

		Sounds.WARNING_LOUD.play(getPitBoss().getSubLevel().getMiddle(), 40);

		for(int i = 0; i < patchCount; i++) {
			int index = random.nextInt(applicableBlocks.size());
			Location spawnLocation = applicableBlocks.get(index).getLocation();
			if(usedLocations.contains(spawnLocation)) {
				i--;
				continue;
			}
			usedLocations.add(spawnLocation);

			Patch patch = new Patch(spawnLocation, radius);
			patch.collapseAfter(warningTime);
			patch.warn(50);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Sounds.COLLAPSE.play(getPitBoss().getSubLevel().getMiddle(), 40);
			}
		}.runTaskLater(PitSim.INSTANCE, warningTime);
	}

	public int getCeilingHeight(Location floor) {

		for(int i = 1; i < 25; i++) {
			Location compare = floor.clone().add(0, i, 0);
			if(compare.getBlock().getType() != Material.AIR) {
				return i;
			}
		}

		return -1;
	}

	public List<Location> getPatch(Location centerLocation, int radius) {
		List<Location> patch = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;
				patch.add(blockLocation.add(0.5, 0, 0.5));
			}
		}

		List<Location> toRemove = new ArrayList<>();
		int height = 5;

		for(Location location : patch) {
			for(int i = 0; i < height; i++) {
				Location compare = location.clone().subtract(0, i, 0);
				Block compareBlock = compare.clone().getBlock();
				if(compareBlock.getType() != Material.AIR && compare.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
					location.subtract(0, i, 0);
					break;
				}


				if(i == (height - 1)) {

					for(int j = 0; j < height; j++) {
						Location upCompare = location.clone().add(0, j, 0);
						if(upCompare.clone().add(0, 1, 0).getBlock().getType() != Material.AIR && upCompare.getBlock().getType() == Material.AIR) {
							location.add(0, j + 1, 0);
							break;
						}

						if(j == (height - 1)) {
							toRemove.add(location);
						}
					}

				}
			}
		}

		for(Location location : toRemove) {
			patch.remove(location);
		}

		return patch;
	}

	public class Patch {
		public Location center;
		public int radius;
		public List<Location> patchLocations;
		public int ceilingHeight;

		public Patch(Location center, int radius) {
			this.center = center;
			this.radius = radius;
			this.ceilingHeight = getCeilingHeight(center);

			patchLocations = getPatch(center.clone().add(0, ceilingHeight, 0), radius);
		}

		public void collapseAfter(int ticks) {
			new BukkitRunnable() {
				@Override
				public void run() {
					collapse();
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);
		}

		public void collapse() {
			int ticks = Misc.getFallTime(ceilingHeight - 1);
			Random random = new Random();

			for(Location location : patchLocations) {
				PacketBlock packetBlock = new PacketBlock(Material.BARRIER, (byte) 0, location);
				packetBlock.setViewers(getViewers());
				packetBlock.spawnBlock();
				packetBlock.removeAfter(ticks + random.nextInt(20 * 5));

				FallingBlock fallingBlock = new FallingBlock(location.getBlock().getType(), location.getBlock().getData(), location.subtract(0, 1, 0));
				fallingBlock.setViewers(getViewers());
				fallingBlock.spawnBlock();
				fallingBlock.removeAfter(ticks);

			}

			new BukkitRunnable() {
				@Override
				public void run() {
					Sounds.COLLAPSE2.play(center, 40);
					getPitBoss().getBoss().getWorld().getNearbyEntities(center, radius, 5, radius).forEach(entity -> {
						if(Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) {
							Player player = (Player) entity;
							DamageManager.createIndirectAttack(getPitBoss().getBoss(), player, damage);
							Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, ticks, 1, false, false);
						}
					});
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);
		}

		public void warn(int duration) {
			RedstoneParticle particle = new RedstoneParticle(false, false);

			new BukkitRunnable() {
				int i = 0;

				@Override
				public void run() {

					for(int degrees = 0; degrees < 360; degrees += 5) {
						double x = Math.cos(Math.toRadians(degrees)) * radius;
						double z = Math.sin(Math.toRadians(degrees)) * radius;

						for(Player viewer : getViewers()) {
							EntityPlayer nmPlayer = ((CraftPlayer) viewer).getHandle();
							particle.display(nmPlayer, center.clone().add(x, 1.4, z), ParticleColor.DARK_RED);
						}
					}

					if(i >= duration) cancel();
					i += 5;
				}
			}.runTaskTimer(PitSim.INSTANCE, 0, 5);
		}
	}
}
