package dev.kyro.pitsim.adarkzone;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

public abstract class PitMob {

	public Creature mob;
	public Player target;

	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();
	public abstract void spawn(Location spawnLocation);

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
