package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRainAbility extends RoutinePitBossAbility {

	public int radius;
	public int blockCount;
	public Material material;
	public Sounds.SoundEffect soundEffect;
	public byte data;
	public double damage;

	public BlockRainAbility(double routineWeight, int radius, int blockCount, Material material, byte data, Sounds.SoundEffect soundEffect, double damage) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
		this.material = material;
		this.data = data;
		this.damage = damage;
		this.soundEffect = soundEffect;
	}

	@Override
	public void onRoutineExecute() {

		Location centerLocation = pitBoss.boss.getLocation().clone().subtract(0, 1, 0);
		List<Location> applicableLocations = new ArrayList<>();

		int rotations = 0;

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);
				if(blockLocation.distance(centerLocation) > radius) continue;
				blockLocation.add(0, 7, 0);

				if(blockLocation.getBlock().getType() != Material.AIR) continue;
				applicableLocations.add(blockLocation.add(0, 0, 0));

				rotations++;
			}
		}
//		for(Location applicableLocation : applicableLocations) {
//			System.out.println(applicableLocation);
//		}

		List<Location> usedLocations = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < blockCount; i++) {
			Location randomLocation = applicableLocations.get(random.nextInt(applicableLocations.size()));
			if(usedLocations.contains(randomLocation)) {
//				i--;
				continue;
			}

			usedLocations.add(randomLocation);
			int maxHeight = getMaxHeight(randomLocation);
			if(maxHeight < 1) continue;
			int addedHeight = random.nextInt(maxHeight);
			randomLocation.add(0, addedHeight, 0);

			new BukkitRunnable() {
				@Override
				public void run() {
					FallingBlock fallingBlock = new FallingBlock(material, data, randomLocation);
					fallingBlock.setViewers(getViewers());
					fallingBlock.spawnBlock();

					//1000 - 5.102d + 989.960 W-1(-0.36786e0.0051538d)
					int totalHeight = 0;
					for(int j = 0; j < 15; j++) {
						Location floorLocation = randomLocation.clone().subtract(0, j, 0);
						if(floorLocation.getBlock().getType() != Material.AIR) {
							totalHeight = j;
							break;
						}
					}

					if(totalHeight < 1) {
						fallingBlock.removeBlock();
						return;
					}

//					int fallTime = getFallTime(totalHeight);
//					System.out.println("Total Height: " + totalHeight);
//					System.out.println(fallTime);

					int fallTime = getFallTime(totalHeight - 2);

					randomLocation.subtract(0, totalHeight - 1, 0);

					new BukkitRunnable() {
						@Override
						public void run() {
							soundEffect.play(randomLocation, 40);
							for(Entity nearbyEntity : randomLocation.getWorld().getNearbyEntities(randomLocation, 1, 1, 1)) {
								if(!(nearbyEntity instanceof Player)) continue;
								Player player = Bukkit.getPlayer(nearbyEntity.getUniqueId());
								if(player == null) continue;

								player.damage(damage, pitBoss.boss);
							}
						}
					}.runTaskLater(PitSim.INSTANCE, fallTime);

					fallingBlock.removeAfter(fallTime + 3);

				}
			}.runTaskLater(PitSim.INSTANCE, i % 10);

		}
	}

	public int getMaxHeight(Location location) {
		Block[] blocks = new Block[5];

		for(int i = 0; i < 5; i++) blocks[i] = location.clone().add(0, i + 1, 0).getBlock();

		int solidIndex = -1;
		for(int i = 0; i < 5; i++) {
			if(blocks[i].getType() == Material.AIR) {
				solidIndex = i;
			}
		}

		return solidIndex + 1;
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

	public int getFallTime(int totalHeight) {
		final double gravity = -0.03999999910593033;
		final double drag = 0.9800000190734863;

		double locY = 0;
		double motY = 0;

		int ticks = 0;

		while(locY <= totalHeight) {
			locY += motY;
			motY *= drag;
			motY -= gravity;

			ticks++;

//			System.out.println("LocY: " + locY + " : " + (totalHeight));

		}

		return ticks;
	}
}
