package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CollapseAbility extends RoutinePitBossAbility {
	public int radius;

	public CollapseAbility(double routineWeight, int radius) {
		super(routineWeight);
		this.radius = radius;
	}

	@Override
	public void onRoutineExecute() {
		int ceilingHeight = getCeilingHeight();

		List<Location> patch = getPatch(pitBoss.getSubLevel().getMiddle().clone().add(0, ceilingHeight, 0), radius);

		for(Location location : patch) {
			FallingBlock fallingBlock = new FallingBlock(location.getBlock().getType(), location.getBlock().getData(), location);
			fallingBlock.setViewers(getViewers());
			fallingBlock.spawnBlock();
			fallingBlock.removeAfter(40);
		}
	}


	public int getCeilingHeight() {
		Location middle = pitBoss.getSubLevel().getMiddle();

		for(int i = 1; i < 15; i++) {
			Location compare = middle.clone().add(0, i, 0);
			if(compare.getBlock().getType() != Material.AIR) {
				return i;
			}
		}

		return -1;
	}

	public List<Location> getPatch(Location centerLocation, int radius) {
		List<Location> patch = new ArrayList<>();

		for(int x = -1 * radius; x < radius + 1; x++) {
			for(int z = -1 * radius; z < radius + 1; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;
				patch.add(blockLocation);
			}
		}

		List<Location> toRemove = new ArrayList<>();

		for(Location location : patch) {
			for(int i = 0; i < centerLocation.getY() / 2; i++) {
				Location compare = location.subtract(0, i, 0);
				if(compare.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) break;

				if(i == centerLocation.getY() / 2 - 1) {
					toRemove.add(location);
				}
			}
		}

		for(Location location : toRemove) {
			patch.remove(location);
		}

		return patch;
	}
}
