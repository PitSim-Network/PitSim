package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.PitMob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PitZombie extends PitMob {

	public PitZombie() {
	}

	@Override
	public int getMaxHealth() {
		return 0;
	}

	@Override
	public int getSpeedAmplifier() {
		return 0;
	}

	@Override
	public void spawn(Location spawnLocation) {

	}
}

// Path: src\main\java\dev\kyro\pitsim\adarkzone\mobs\PitZombie.java