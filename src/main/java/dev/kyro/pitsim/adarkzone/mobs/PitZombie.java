package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.PitMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class PitZombie extends PitMob {

	public PitZombie(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.ZOMBIE;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public int getSpeedAmplifier() {
		return 0;
	}
}