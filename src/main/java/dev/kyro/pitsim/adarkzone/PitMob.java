package dev.kyro.pitsim.adarkzone;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class PitMob {

	public Creature mob;
	public Player target;

	public PitMob(Location spawnLocation) {
		spawn(spawnLocation);
	}

	public abstract EntityType getEntityType();
	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();

	public void onSpawn() {}

	public void spawn(Location spawnLocation) {
		spawnLocation.getWorld().spawnEntity(spawnLocation, getEntityType());
	}

	public void despawn() {
		mob.remove();
	}

	public void rewardKill(Player killer) {

	}

	public void setTarget(Player target) {
		this.target = target;
		mob.setTarget(target);
	}
}
