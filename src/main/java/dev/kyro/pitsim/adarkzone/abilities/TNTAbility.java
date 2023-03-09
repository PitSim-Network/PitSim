package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.cosmetics.particles.FireworkSparkParticle;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.math.MathUtils;
import dev.kyro.pitsim.misc.math.Point3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class TNTAbility extends PitBossAbility {
	public double damage;

	public TNTAbility(double routineWeight, double damage) {
		super(routineWeight);
		this.damage = damage;
	}

	@Override
	public void onRoutineExecute() {
		Location centerLocation = getPitBoss().boss.getLocation().add(0, 3, 0); // replace with tnt explosion Location
		createExplosion(centerLocation);
	}

	public void createExplosion(Location centerLocation) {
		FireworkSparkParticle particle = new FireworkSparkParticle();
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
				for(Player player : Misc.getNearbyRealPlayers(testLocation, 1)) DamageManager.createAttack(player, damage);
				particle.display(Misc.getNearbyRealPlayers(displayLocation, 50), displayLocation);
			}
		}
	}
}

