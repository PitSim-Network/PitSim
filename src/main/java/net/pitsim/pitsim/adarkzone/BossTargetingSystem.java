package net.pitsim.pitsim.adarkzone;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.enchants.tainted.uncommon.Fearmonger;
import net.pitsim.pitsim.enums.PitEntityType;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class BossTargetingSystem {
	public double healthWeight = 1.5;
	public double distanceWeight = 6.0;
	public double angleWeight = 4.0;

	public PitBoss pitBoss;
	public Player target;

	public long lastTickWithTarget = PitSim.currentTick;

	public BossTargetingSystem(PitBoss pitBoss) {
		this.pitBoss = pitBoss;
	}

	public void assignTarget() {
		Player target = findTarget();
		if(target == null) {
			if(PitSim.currentTick - lastTickWithTarget > 200) {
				pitBoss.remove();
				pitBoss.alertDespawn();
				return;
			}
		} else {
			lastTickWithTarget = PitSim.currentTick;
		}
		setTarget(target);
	}

	public void setTarget(Player target) {
		this.target = target;

		if(!pitBoss.getNpcBoss().isSpawned() || target == null) return;
		pitBoss.getNpcBoss().getNavigator().setTarget(target, true);
	}

	public Player findTarget() {
		SubLevel subLevel = pitBoss.getSubLevel();
		Location subLevelMiddle = subLevel.getMiddle();

		List<Player> potentialTargets = new ArrayList<>();
		for(Entity entity : subLevelMiddle.getWorld().getNearbyEntities(subLevelMiddle, subLevel.spawnRadius, 20, subLevel.spawnRadius)) {
			if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER) || !Misc.isValidMobPlayerTarget(entity)) continue;
			Player player = (Player) entity;
			if(Fearmonger.isImmune(player)) continue;
			potentialTargets.add(player);
		}

		double bestReward = Double.NEGATIVE_INFINITY;
		Player bestTarget = null;
		for(Player player : potentialTargets) {
			double reward = rewardFunction(player);
			if(reward > bestReward) {
				bestReward = reward;
				bestTarget = player;
			}
		}

		return bestTarget;
	}

	//reward function to find the best target
	private double rewardFunction(Player player) {
		Vector distanceVector = player.getLocation().toVector().subtract(pitBoss.getBoss().getLocation().toVector());
		Vector bossDirectionVector = pitBoss.getBoss().getLocation().getDirection();

		double scaledHealth = (20 - player.getHealth()) / 20;
		double scaledDistance = ((pitBoss.getReach() * 2) - distanceVector.length()) / (pitBoss.getReach() * 2);
		double scaledAngle = (Math.PI - distanceVector.angle(bossDirectionVector)) / Math.PI;

//		DecimalFormat df = new DecimalFormat("0.#");
//		double health = healthWeight * scaledHealth;
//		double distance = distanceWeight * scaledDistance;
//		double angle = angleWeight * scaledAngle;
//		String healthColor = health < 1 ? "&c" : health < 2 ? "&e" : "&a";
//		String distanceColor = distance < 2 ? "&c" : distance < 4 ? "&e" : "&a";
//		String angleColor = angle < 1.5 ? "&c" : angle < 3 ? "&e" : "&a";
//		AOutput.broadcast("&7" + player.getName() + " health: " + healthColor + df.format(health) + " &7distance: " +
//				distanceColor + df.format(distance) + " &7angle: " + angleColor + df.format(angle));

		return healthWeight * scaledHealth +
				distanceWeight * scaledDistance +
				angleWeight * scaledAngle;
	}
}

