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

public class SubLevel {
	public SubLevelType subLevelType;

	private Location middle;
	private List<Location> spawnableLocations = new ArrayList<>();
	private String placeholder;

//	Boss related fields
	public Class<? extends PitBoss> bossClass;
	public PitBoss pitBoss;
	private boolean isBossSpawned = false;
	private ItemStack spawnItem;
	private int currentDrops = 0;
	private int requiredDropsToSpawn;

	//	Mob related fields
	public Class<? extends PitMob> mobClass;
	public List<PitMob> mobs = new ArrayList<>();
	public int maxMobs;
	public int spawnRadius;

	public SubLevel(SubLevelType subLevelType, Class<? extends PitBoss> bossClass, Class<? extends PitMob> mobClass,
					Location middle, int maxMobs, int spawnRadius, int requiredDropsToSpawn, String placeholder) {
		this.subLevelType = subLevelType;
		this.bossClass = bossClass;
		this.mobClass = mobClass;
		this.middle = middle;
		this.maxMobs = maxMobs;
		this.spawnRadius = spawnRadius;
		this.requiredDropsToSpawn = requiredDropsToSpawn;
		this.placeholder = placeholder;
		identifySpawnableLocations();

	}

	public void tick() {
		if(Math.random() < 0.75) return;
		int newMobsNeeded = maxMobs - mobs.size();
		for(int i = 0; i < Math.min(newMobsNeeded, 3); i++) {
			if (!isBossSpawned) spawnMob();
		}
	}

	public Location getMobSpawnLocation() {
		return spawnableLocations.get(new Random().nextInt(spawnableLocations.size()));
	}

	public void identifySpawnableLocations() {
		for(int x = -spawnRadius; x < spawnRadius + 1; x++) {
			for(int z = -spawnRadius; z < spawnRadius + 1; z++) {
				Location location = new Location(MapManager.getDarkzone(), middle.getBlockX() + x, middle.getBlockY(), middle.getBlockZ() + z);
				if(location.distance(middle) > spawnRadius) continue;
				location.add(0, -3, 0);
				if(!isSpawnableLocation(location)) continue;
				spawnableLocations.add(location);
			}
		}
	}

	public boolean isSpawnableLocation(Location location) {
		boolean canSpawn = false;
		for(int i = 0; i < 6; i++) {
			Block block = location.getBlock();
			if(block.getType() == Material.AIR) {
				location.add(0, 1, 0);
				continue;
			}
			Block blockAbove = location.clone().add(0, 1, 0).getBlock();
			if(blockAbove.getType() != Material.AIR) {
				location.add(0, 1, 0);
				continue;
			}
			canSpawn = true;
			break;
		}
		if(!canSpawn) return false;
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
			Constructor<? extends PitMob> constructor = mobClass.getConstructor(Location.class);
			Location location = getMobSpawnLocation();
			pitMob = constructor.newInstance(location);
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		mobs.add(pitMob);
	}

	public void spawnBoss(Player summoner) {
		if(isBossSpawned) throw new RuntimeException();
		try {
			Constructor<? extends PitBoss> constructor = bossClass.getConstructor(Player.class);
			pitBoss = constructor.newInstance(summoner);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		System.out.println("Spawned boss");
		isBossSpawned = true;
		disableMobs();
	}

	public void bossDeath() {
		isBossSpawned = false;
		System.out.println("test");

	}

	public void disableMobs() {
		for(PitMob mob : mobs) mob.despawn();
		mobs.clear();
	}

	/**
	 * @return ItemStack required to spawn boss
	 */
	public ItemStack getSpawnItem() {
		return spawnItem;
	}

	/**
	 * @param spawnItem ItemStack required to spawn boss
	 */
	public void setSpawnItem(ItemStack spawnItem) {
		this.spawnItem = spawnItem;
	}

	public SubLevelType getSubLevelType() {
		return subLevelType;
	}

	public boolean isBossSpawned() {
		return isBossSpawned;
	}

	public int getCurrentDrops() {
		return currentDrops;
	}

	public void setCurrentDrops(int currentDrops) {
		this.currentDrops = currentDrops;
	}

	public int getRequiredDropsToSpawn() {
		return requiredDropsToSpawn;
	}

	public Location getMiddle() {
		return middle;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public Location getBossSpawnLocation() {
		return getMiddle().clone().add(0, 2, 0);
	}

	public Location getSpawnerLocation() {
		return getMiddle().clone().add(0, 1, 0);
	}
}
