package dev.kyro.pitsim.adarkzone;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class PitMob {

	public LivingEntity mob;

//	Targeting
	public Player player;

	public abstract int getMaxHealth();
	public abstract double getReach();
	public abstract double getReachRanged();

	public void remove() {
		mob.remove();
	}
}
