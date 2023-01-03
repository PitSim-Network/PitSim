package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class SubLevel {
	private Location middle;
	private Location bossSpawnLocation;
	private List<Location> spawnableLocations = new ArrayList<>();

//	Mob related fields
	public List<PitMob> mobs = new ArrayList<>();

//	Boss related fields
	public boolean isBossSpawned = false;
	public PitBoss pitBoss;
	public ItemStack spawnItem;
	public int currentDrops = 0;

	public SubLevel() {
		this.middle = createMiddle();
		this.bossSpawnLocation = middle.clone().add(0, 2, 0);

		identifySpawnableLocations();
	}

	//	Mobs
	public abstract Class<? extends PitMob> getMob();
	public abstract int getMaxMobs();
	public abstract Location createMiddle();
	public abstract int getSpawnRadius();

//	Bosses
	public abstract Class<? extends PitBoss> getBoss();
	public abstract int getRequiredDropsToSpawn();

	public void tick() {
		if(Math.random() < 0.75) return;
		int newMobsNeeded = getMaxMobs() - mobs.size();
		for(int i = 0; i < Math.min(newMobsNeeded, 3); i++) spawnMob();
	}

	public Location getMobSpawnLocation() {
		return spawnableLocations.get(new Random().nextInt(spawnableLocations.size()));
	}

	public void identifySpawnableLocations() {
		for(int x = -getSpawnRadius(); x < getSpawnRadius() + 1; x++) {
			for(int z = -getSpawnRadius(); z < getSpawnRadius() + 1; z++) {
				Location location = new Location(MapManager.getDarkzone(), x, getMiddle().getBlockY(), z);
				if(location.distance(getMiddle()) > getSpawnRadius()) continue;
				location.add(0, -3, 0);
				if(!isSpawnableLocation(location)) continue;
				spawnableLocations.add(location);
			}
		}
	}

	public boolean isSpawnableLocation(Location location) {
		boolean foundGround = false;
		boolean foundSpawnableSpace = false;
		for(int i = 0; i < 7; i++) {
			Block block = location.getBlock();
			if(block.getType() != Material.AIR) {
				foundGround = true;
				continue;
			}
			Block blockAbove = location.clone().add(0, 1, 0).getBlock();
			if(blockAbove.getType() != Material.AIR) {
				location.add(0, 1, 0);
				continue;
			}
			foundSpawnableSpace = true;
			break;
		}
		if(!foundGround || !foundSpawnableSpace) return false;
		boolean foundCeiling = false;
		for(int i = 0; i < 15; i++) {
			Block block = location.clone().add(0, i + 2, 0).getBlock();
			if(block.getType() == Material.AIR) continue;
			foundCeiling = true;
			break;
		}
		return foundCeiling;
	}

	public void spawnMob() {
		PitMob pitMob;
		try {
			Constructor<? extends PitMob> constructor = getMob().getConstructor(Location.class);
			pitMob = constructor.newInstance(getMobSpawnLocation());
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		mobs.add(pitMob);
	}

	public void spawnBoss(Player summoner) {
		if(isBossSpawned) throw new RuntimeException();
		try {
			Constructor<? extends PitBoss> constructor = getBoss().getConstructor(Player.class);
			pitBoss = constructor.newInstance(summoner);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		isBossSpawned = true;
		disableMobs();
	}

	public void bossDeath() {
		isBossSpawned = false;
	}

	public void disableMobs() {
		for(PitMob mob : mobs) mob.despawn();
		mobs.clear();
	}

	public Location getMiddle() {
		return middle;
	}

	public Location getBossSpawnLocation() {
		return bossSpawnLocation;
	}

	public ItemStack getSpawnItem() {
		return spawnItem;
	}
}
