package dev.kyro.pitsim.adarkzone;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class TargetingSystem {
	public double healthWeight = 0.7;
	public double distanceWeight = 0.4;
	public double angleWeight = 1.0;

	public State targetingState;
	public PitBoss pitBoss;
	public Player target;

//	TODO: Figure out if ranged attacks are all going to be shooting bows or if we are going to abstract and allow other stuff
//	TODO: (maybe like snowballs, fireballs, particle beams, homing particles, thrown entities)
	public enum State {
		ATTACKING_MELEE,
		ATTACKING_RANGED
	}

	public TargetingSystem(PitBoss pitBoss, Player target) {
		this.pitBoss = pitBoss;
		this.target = target;
	}

	public Player findTarget(State targetingState) {
		double radius = pitBoss.getReach();

		if(targetingState == State.ATTACKING_MELEE) {
			radius = pitBoss.getReach();
		} else if(targetingState == State.ATTACKING_RANGED) {
			radius = pitBoss.getReachRanged();
		}

		List<Player> playersInRadius = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(radius, radius, radius)) {
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			playersInRadius.add(player);
		}

		Vector pitBossDirection = pitBoss.boss.getLocation().getDirection();
		double pitBossAngle = Math.atan2(pitBossDirection.getX(), pitBossDirection.getZ());

		double bestReward = 0;
		Player bestTarget = null;
		for(Player player : playersInRadius) {

			double reward = rewardFunction(player, pitBossAngle);
			if(reward > bestReward) {
				bestReward = reward;
				bestTarget = player;
			}
		}

		return bestTarget;
	}

	//reward function to find the best target
	private double rewardFunction(Player player, double pitBossAngle) {

		Vector playerDirection = player.getLocation().getDirection();
		double playerAngle = Math.atan2(playerDirection.getX(), playerDirection.getZ());

		double angleBetween = Math.abs(pitBossAngle - playerAngle);

		double distance = player.getLocation().distance(pitBoss.boss.getLocation());

		double health = player.getHealth();

		double normalizedHealth = 20 / health;
		double normalizedDistance = pitBoss.getReach() / distance;
		double normalizedAngle = 1 / angleBetween;

		return healthWeight * normalizedHealth + distanceWeight * normalizedDistance + angleWeight * normalizedAngle;
	}
}
