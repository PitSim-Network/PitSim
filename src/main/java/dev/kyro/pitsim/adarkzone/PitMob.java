package dev.kyro.pitsim.adarkzone;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class PitMob {

	public LivingEntity mob;

//	Targeting
	public Player player;

	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();
	public abstract void spawn(Location spawnLocation);

	public void remove() {
		mob.remove();
	}
}
