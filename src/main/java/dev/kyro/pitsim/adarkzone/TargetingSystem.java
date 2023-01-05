package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class TargetingSystem {
	public double healthWeight = 0.7;
	public double distanceWeight = 0.4;
	public double angleWeight = 1.0;

	public State targetingState = State.ATTACKING_MELEE;
	public PitBoss pitBoss;
	public Player target;

	public BukkitTask runnable;

//	TODO: Figure out if ranged attacks are all going to be shooting bows or if we are going to abstract and allow other stuff
//	TODO: (maybe like snowballs, fireballs, particle beams, homing particles, thrown entities)
	public enum State {
		ATTACKING_MELEE,
		ATTACKING_RANGED
	}

	public TargetingSystem(PitBoss pitBoss) {
		this.pitBoss = pitBoss;

		start();
	}

	public void pickTarget() {
		Player target = findTarget();
		if(target == null) {
			for(UUID uuid : pitBoss.damageMap.keySet()) {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					AOutput.send(player, "Boss depawned becaUse nobody was in range");
				}

			}
		}
		setTarget(target);
	}

	public void setTarget(Player target) {
		this.target = target;
		pitBoss.npcBoss.getNavigator().setTarget(target, true);
	}

	public Player findTarget() {
		double radius = pitBoss.getReach();

		if(targetingState == State.ATTACKING_MELEE) {
			radius = pitBoss.getReach();
		} else if(targetingState == State.ATTACKING_RANGED) {
			radius = pitBoss.getReachRanged();
		}

		List<Player> playersInRadius = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(radius, radius, radius))  {
			System.out.println("FOUND ENTITY");
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			playersInRadius.add(player);
		}
		if (playersInRadius.size() == 0) {
			for(Entity entity : pitBoss.boss.getNearbyEntities(radius*3, radius*3, radius*3))  {
				System.out.println("FOUND ENTITY");
				if(!(entity instanceof Player)) continue;
				Player player = (Player) entity;
				playersInRadius.add(player);
			}
		}
		if (playersInRadius.size() == 0) {
			SubLevel subLevel = pitBoss.getSubLevel();
			Location location = subLevel.getMiddle();
			for(Entity entity : location.getWorld().getNearbyEntities(location, 35, 20, 35))  {
				System.out.println("FOUND ENTITY");
				if(!(entity instanceof Player)) continue;
				Player player = (Player) entity;
				playersInRadius.add(player);
			}
		}

		if (playersInRadius.size() == 0) {
			return null;
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

	public void start() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {

			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5L);
	}

	public void stop() {
		runnable.cancel();
	}
}

