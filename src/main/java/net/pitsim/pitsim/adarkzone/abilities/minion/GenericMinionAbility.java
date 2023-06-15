package net.pitsim.pitsim.adarkzone.abilities.minion;

import net.pitsim.pitsim.adarkzone.SubLevelType;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class GenericMinionAbility extends MinionAbility {

	public int spawnAmount;
	public int radius = -1;

	public GenericMinionAbility(double routineWeight, SubLevelType type, int spawnAmount, int maxMobs, int spawnRadius) {
		this(routineWeight, type, spawnAmount, maxMobs);
		this.radius = spawnRadius;
	}

	public GenericMinionAbility(double routineWeight, SubLevelType type, int spawnAmount, int maxMobs) {
		super(routineWeight, type, maxMobs);
		this.spawnAmount = spawnAmount;
	}

	@Override
	public void onRoutineExecute() {
		spawnMobs(radius == -1 ? null : getSpawnLocation(radius), spawnAmount);
	}

	@Override
	public boolean shouldExecuteRoutine() {
		return subLevelType.getSubLevel().mobs.size() < maxMobs;
	}

	public Location getSpawnLocation(int radius) {
		Random random = new Random();
		Location location = null;

		while(location == null || location.getBlock().getType() != Material.AIR) {
			int randomX = random.nextInt(radius - (-1 * radius)) + (-1 * radius);
			int randomZ = random.nextInt(radius - (-1 * radius)) + (-1 * radius);

			location = getPitBoss().getBoss().getLocation().clone().add(randomX, 0, randomZ);
		}
		return location;
	}
}
