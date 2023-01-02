package dev.kyro.pitsim.adarkzone;

import org.bukkit.Location;

public abstract class SubLevel {
	private Location middle;

	public boolean isBossSpawned = false;

	public SubLevel() {
		this.middle = createMiddle();
	}

	//	Mobs
	public abstract Class<? extends PitMob> getMob();
	public abstract int getMaxMobs();
	public abstract Location createMiddle();
	public abstract int getSpawnRadius();

//	Bosses
	public abstract Class<? extends PitBoss> getBoss();
	public abstract int getRequiredDropsToSpawn();

	public Location getMiddle() {
		return middle;
	}
}
