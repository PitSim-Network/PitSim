package dev.kyro.pitsim.adarkzone;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enchants.tainted.uncommon.Fearmonger;
import dev.kyro.pitsim.enchants.tainted.chestplate.Terror;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class MobTargetingSystem {
	public static final int MAX_MOBS_PER_PLAYER = 3;
	public Map<PitMob, Long> changeTargetCooldown = new HashMap<>();

	public double distanceWeight = 1.0;
	public double persistenceWeight = 0.1;
	public double otherMobsTargetingWeight = -0.5;

	public SubLevel subLevel;
	public BukkitTask runnable;

	public MobTargetingSystem(SubLevel subLevel) {
		this.subLevel = subLevel;
	}

	public void assignTargets() {
		List<PitMob> pitMobs = new ArrayList<>(subLevel.mobs);
//		Collections.shuffle(pitMobs);

		for(PitMob pitMob : pitMobs) {
			Map<Player, Integer> currentTargetMap = getCurrentTargets();
			assignTarget(pitMob, currentTargetMap);
		}
	}

	public Map<Player, Integer> getCurrentTargets() {
		Map<Player, Integer> currentTargetMap = new HashMap<>();
		for(PitMob pitMob : subLevel.mobs) {
			Player target = pitMob.getTarget();
			currentTargetMap.putIfAbsent(target, 0);
			currentTargetMap.put(target, currentTargetMap.get(target) + 1);
		}
		return currentTargetMap;
	}

	public void assignTarget(PitMob pitMob, Map<Player, Integer> currentTargetMap) {
		Player bestTarget = pitMob.getTarget();
		if(bestTarget != null) {
			double targetDistanceFromMid = Double.MAX_VALUE;
			if(pitMob.getMob().getWorld() == bestTarget.getWorld()) targetDistanceFromMid = pitMob.getMob().getLocation().distance(bestTarget.getLocation());
			if(targetDistanceFromMid > subLevel.spawnRadius || Fearmonger.isImmune(bestTarget)) bestTarget = null;
		}

		List<Player> potentialTargets = new ArrayList<>();
		potentialTargets.add(null);
		Location subLevelMiddle = subLevel.getMiddle();
		for(Entity entity : subLevelMiddle.getWorld().getNearbyEntities(subLevelMiddle, subLevel.spawnRadius, 20, subLevel.spawnRadius)) {
			if(!(entity instanceof Player) || entity == pitMob.getMob()) continue;
			Player player = (Player) entity;
			if(VanishAPI.isInvisible(player) || Fearmonger.isImmune(player)) continue;
			potentialTargets.add(player);
		}

		double bestReward = Double.NEGATIVE_INFINITY;
		for(Player candidate : potentialTargets) {
			int currentTargets = currentTargetMap.getOrDefault(candidate, 0);
			double reward = rewardFunction(pitMob, currentTargets, pitMob.getTarget(), candidate);
			if(reward > bestReward) {
				bestReward = reward;
				bestTarget = candidate;
			}
		}

		if(bestTarget != null)
			pitMob.getMob().getWorld().playEffect(pitMob.getMob().getLocation().add(0, 2, 0), Effect.VILLAGER_THUNDERCLOUD, 1);
		if(bestTarget == null) {
			if(changeTargetCooldown.getOrDefault(pitMob, 0L) + 60 > PitSim.currentTick) return;
			changeTargetCooldown.put(pitMob, PitSim.currentTick);
		}
		pitMob.setTarget(bestTarget);
	}

	//reward function to find the best target
	private double rewardFunction(PitMob pitMob, int mobsTargeting, Player currentTarget, Player candidate) {
		double scaledDistance = 0;
		double scaledPersistence = currentTarget == candidate && candidate != null ? 1 : 0;
		double scaledTargets = 0;
		if(candidate != null) {
			scaledDistance = ((subLevel.spawnRadius * 2) - candidate.getLocation().distance(pitMob.getMob().getLocation())) / (subLevel.spawnRadius * 2);
			scaledTargets = Math.pow(mobsTargeting, 1.3) / MAX_MOBS_PER_PLAYER;
			scaledTargets *= Terror.getAvoidanceMultiplier(candidate);
		}

//		double total = scaledDistance * distanceWeight +
//				scaledPersistence * persistenceWeight +
//				scaledTargets * otherMobsTargetingWeight;
//		DecimalFormat decimalFormat = new DecimalFormat("0.#");
//		System.out.println(
//				(candidate == null ? "null" : candidate.getName()) + " " +
//				decimalFormat.format(scaledDistance * distanceWeight) + " " +
//				decimalFormat.format(scaledPersistence * persistenceWeight) + " " +
//				decimalFormat.format(scaledTargets * otherMobsTargetingWeight) + " " +
//				decimalFormat.format(total)
//		);

		return scaledDistance * distanceWeight +
				scaledPersistence * persistenceWeight +
				scaledTargets * otherMobsTargetingWeight;
	}
}

