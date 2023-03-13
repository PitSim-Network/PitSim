package dev.kyro.pitsim.adarkzone.abilities.abilitytypes;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.misc.BlockData;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class BlockRainAbility extends PitBossAbility {

	public int radius;
	public int blockCount;
	public Map<BlockData, Double> blocks;
	public double damage;

	public BlockRainAbility(double routineWeight, int radius, int blockCount, Map<BlockData, Double> blocks, double damage) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
		this.blocks = blocks;
		this.damage = damage;
	}

	public BlockRainAbility(double routineWeight, int radius, int blockCount, BlockData block, double damage) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
		this.blocks = new HashMap<>();
		this.damage = damage;

		blocks.put(block, 1.0);
	}

	public abstract void onBlockLand(FallingBlock block, Location location);

	@Override
	public void onRoutineExecute() {

		Location centerLocation = getPitBoss().boss.getLocation().clone().subtract(0, 1, 0);
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
					BlockData blockData = getBlock(blocks);
					FallingBlock fallingBlock = new FallingBlock(blockData.material, blockData.data, randomLocation);
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

					int fallTime = Misc.getFallTime(totalHeight - 2);

					randomLocation.subtract(0, totalHeight - 1, 0);

					new BukkitRunnable() {
						@Override
						public void run() {
							onBlockLand(fallingBlock, randomLocation);
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

	public BlockData getBlock(Map<BlockData, Double> blockMap) {
		return Misc.weightedRandom(blockMap);
	}
}
