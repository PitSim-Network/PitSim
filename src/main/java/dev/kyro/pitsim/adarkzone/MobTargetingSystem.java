package dev.kyro.pitsim.adarkzone;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class MobTargetingSystem {
	public double healthWeight = 0.7;
	public double distanceWeight = 0.4;
	public double angleWeight = 1.0;

	public SubLevel subLevel;
	public BukkitTask runnable;

	public MobTargetingSystem(SubLevel subLevel) {
		this.subLevel = subLevel;
	}

	public void assignTargets() {
		Map<Player, Integer> currentTargetMap = getCurrentTargets();
		for(PitMob pitMob : subLevel.mobs) assignTarget(pitMob, new HashMap<>(currentTargetMap));
	}

	public Map<Player, Integer> getCurrentTargets() {
		Map<Player, Integer> currentTargetMap = new HashMap<>();
		for(PitMob mob : subLevel.mobs) {
			Player target = mob.getTarget();
			currentTargetMap.putIfAbsent(target, 0);
			currentTargetMap.put(target, currentTargetMap.get(target) + 1);
		}
		return currentTargetMap;
	}

	public void assignTarget(PitMob pitMob, Map<Player, Integer> currentTargetMap) {
		double radius = pitBoss.getReach();

		if(targetingState == State.ATTACKING_MELEE) {
			radius = pitBoss.getReach();
		} else if(targetingState == State.ATTACKING_RANGED) {
			radius = pitBoss.getReachRanged();
		}

		List<Player> playersInRadius = new ArrayList<>();
		SubLevel subLevel = pitBoss.getSubLevel();
		Location subLevelMiddle = subLevel.getMiddle();
		double bossToMidDistance = pitBoss.boss.getLocation().distance(subLevelMiddle);
		if(bossToMidDistance < subLevel.spawnRadius) {
			for(Entity entity : pitBoss.boss.getNearbyEntities(radius, radius, radius)) {
				if(!(entity instanceof Player)) continue;
				Player player = (Player) entity;
				if(VanishAPI.isInvisible(player)) continue;
				playersInRadius.add(player);
			}
			if(playersInRadius.isEmpty()) {
				for(Entity entity : pitBoss.boss.getNearbyEntities(radius * 3, radius * 3, radius * 3)) {
					if(!(entity instanceof Player)) continue;
					Player player = (Player) entity;
					if(VanishAPI.isInvisible(player)) continue;
					playersInRadius.add(player);
				}
			}
		}
		if(playersInRadius.isEmpty()) {
			for(Entity entity : subLevelMiddle.getWorld().getNearbyEntities(subLevelMiddle, subLevel.spawnRadius, 20, subLevel.spawnRadius)) {
				if(!(entity instanceof Player) || entity == pitBoss.boss) continue;
				Player player = (Player) entity;
				if(VanishAPI.isInvisible(player)) continue;
				playersInRadius.add(player);
			}
		}

		if(playersInRadius.isEmpty()) return null;

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

