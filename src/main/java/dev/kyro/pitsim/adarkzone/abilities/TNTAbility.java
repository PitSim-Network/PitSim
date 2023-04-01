package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.cosmetics.particles.FireworkSparkParticle;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.math.MathUtils;
import dev.kyro.pitsim.misc.math.Point3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TNTAbility extends PitBossAbility {
	public double damage;
	public List<TNTPrimed> entities = new ArrayList<>();

	public TNTAbility(double routineWeight, double damage) {
		super(routineWeight);
		this.damage = damage;
	}

	@Override
	public void onRoutineExecute() {
		Location spawnLocation = getPitBoss().boss.getLocation().add(0, 3, 0); // replace with tnt explosion Location

		Player target = getPitBoss().bossTargetingSystem.target;
		if(target == null) return;

		TNTPrimed tntPrimed = spawnLocation.getWorld().spawn(spawnLocation, TNTPrimed.class);
		entities.add(tntPrimed);
		Sounds.TNT_PRIME.play(tntPrimed.getLocation(), 40);

		double directDistance = target.getLocation().distance(spawnLocation);
		Vector distanceVector = target.getLocation().toVector().add(new Vector(0, directDistance * 0.5 + 2, 0))
				.subtract(spawnLocation.toVector());
		int fuse = (int) distanceVector.length();
		tntPrimed.setVelocity(distanceVector.normalize().multiply(1.1));
		tntPrimed.setFuseTicks(fuse);

		new BukkitRunnable() {
			@Override
			public void run() {
				createExplosion(tntPrimed.getLocation());
				tntPrimed.remove();
				entities.remove(tntPrimed);
				Sounds.CREEPER_EXPLODE.play(tntPrimed.getLocation(), 40);
			}
		}.runTaskLater(PitSim.INSTANCE, fuse);
	}

	@Override
	public boolean shouldExecuteRoutine() {
		return getPitBoss().bossTargetingSystem.target != null;
	}

	@Override
	public void disable() {
		super.disable();
		for(TNTPrimed tntPrimed : entities) {
			tntPrimed.remove();
		}
	}

	public void createExplosion(Location centerLocation) {
		FireworkSparkParticle particle = new FireworkSparkParticle();
		Map<Player, Integer> hitMap = new HashMap<>();
		for(Point3D point : MathUtils.getSphere(0.5, 50, 1, 1, 1)) {
			Location vectorEnd = centerLocation.clone().add(point.getX(), point.getY(), point.getZ());
			Vector stepVector = vectorEnd.clone().subtract(centerLocation).toVector().normalize().multiply(0.25);
			Location displayLocation = centerLocation.clone();

			int maxParticles = (4 * 12) - new Random().nextInt(5);
			double random = Math.random();
			if(random < 0.2) maxParticles += 4 * 4;
			else if(random < 0.4) maxParticles += 4 * 2;

			for(int i = 0; i < maxParticles; i++) {
				displayLocation.add(stepVector);
				Block block = displayLocation.getBlock();
				if(block != null && block.getType() != Material.AIR) break;

				Location testLocation = displayLocation.clone().add(0, -1, 0);
				for(Player player : Misc.getNearbyRealPlayers(testLocation, 1.5)) {
					int timesHit = hitMap.getOrDefault(player, 0);
					if(timesHit >= 3) continue;
					hitMap.put(player, timesHit + 1);
					DamageManager.createIndirectAttack(getPitBoss().boss, player, damage);
				}
				particle.display(Misc.getNearbyRealPlayers(displayLocation, 50), displayLocation);
			}
		}
	}
}

