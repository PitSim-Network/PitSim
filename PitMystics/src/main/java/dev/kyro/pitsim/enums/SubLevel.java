package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.mobs.PitSkeleton;
import dev.kyro.pitsim.mobs.PitZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public enum SubLevel {
	ZOMBIE_CAVE(1, 20, new Location(Bukkit.getWorld("darkzone"), 56, 53, -95), 25, Arrays.asList(PitZombie.class, PitSkeleton.class));
//	CHARGED_CREEPER,
//	SKELETON,
//	MAGMA_CUBE,
//	WITHER_SKELETON,
//	IRON_GOLEM,
//	SPIDER,
//	CAVE_SPIDER,
//	ENDERMAN,
//	ZOMBIE_PIGMAN;

	public int level;
	public int maxMobs;
	public Location middle;
	public int radius;
	public List<Class> mobs;

	SubLevel(int level, int maxMobs, Location middle, int radius, List<Class> mobs) {
		this.level = level;
		this.maxMobs = maxMobs;
		this.middle = middle;
		this.radius = radius;
		this.mobs = mobs;
	}


}
