package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.abilitytypes.MinionAbility;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class SpiderMinionAbility extends MinionAbility {

	public int spawnAmount;
	public int spawnRadius;

	public SpiderMinionAbility(double routineWeight, int spawnAmount, int maxMobs, int spawnRadius) {
		super(routineWeight, SubLevelType.SPIDER, maxMobs);
		this.spawnAmount = spawnAmount;
		this.spawnRadius = spawnRadius;
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(getSpawnLocation(spawnRadius), spawnAmount);
	}

	public Location getSpawnLocation(int radius) {
		Random random = new Random();
		Location location = null;

		while(location == null || location.getBlock().getType() != Material.AIR) {
			int randomX = random.nextInt(radius - (-1 * radius)) + (-1 * radius);
			int randomZ = random.nextInt(radius - (-1 * radius)) + (-1 * radius);

			location = getPitBoss().boss.getLocation().clone().add(randomX, 0, randomZ);
		}

		return location;
	}

}
