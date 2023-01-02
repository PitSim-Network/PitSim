package dev.kyro.pitsim.adarkzone.sublevels;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.controllers.MapManager;
import org.bukkit.Location;

public class ZombieSubLevel extends SubLevel {
	@Override
	public Class<? extends PitMob> getMob() {
		return PitZombie.class;
	}

	@Override
	public int getMaxMobs() {
		return 20;
	}

	@Override
	public Location createMiddle() {
		return new Location(MapManager.getDarkzone(), 327, 68, -143);
	}

	@Override
	public int getSpawnRadius() {
		return 0;
	}

	@Override
	public Class<? extends PitBoss> getBoss() {
		return PitZombieBoss.class;
	}

	@Override
	public int getRequiredDropsToSpawn() {
		return 0;
	}
}
