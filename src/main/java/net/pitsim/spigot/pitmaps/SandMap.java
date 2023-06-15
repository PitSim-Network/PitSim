package net.pitsim.spigot.pitmaps;

import net.pitsim.spigot.boosters.ChaosBooster;
import net.pitsim.spigot.controllers.objects.PitMap;
import org.bukkit.Location;

public class SandMap extends PitMap {
	public SandMap(String worldName, int rotationDays) {
		super(worldName, rotationDays);
	}

	public static Location mid = new Location(null, 0, 30, 0);

	@Override
	public Location getSpawn() {
		return new Location(world, -11, 73, 11, -135, 0);
	}

	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0, 70, 0);
		spawn.setX(spawn.getX() + (Math.random() * 4 - 2));
		spawn.setZ(spawn.getZ() + (Math.random() * 4 - 2));

		if(ChaosBooster.INSTANCE.isActive()) {
			spawn.add(0, -34, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -29, 0);
		}
		return spawn;
	}

	@Override
	public Location getFromDarkzoneSpawn() {
		return new Location(world, -36, 31, 1, -90, 0);
	}

	@Override
	public Location getMid() {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, 9.5, 73, -2.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -9.5, 73, 0, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, -8.5, 73, -7.5, -45.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, -10.5, 73, -7.5, -55, 10);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, -10.5, 73, -5.5, -62, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, 0, 73, -9.5, 0, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, 5.5, 73, 8.5, 145, 0);
	}

	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, 9.5, 73, 7.5, 125, 0);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 9.5, 73, 1.5, 90, 0);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(null, -53.5, 73, 0.5, -90, 0);
	}

	@Override
	public Location getWelcomeHolo() {
		return new Location(world, -5, 77.6, 5);
	}

	@Override
	public Location getMysticWellHolo() {
		return new Location(world, 8.588, 74.7, 8.576);
	}

	@Override
	public Location getKitsHolo() {
		return new Location(world, 9.388, 74.095, 7.439);
	}

	@Override
	public Location getEnderchest1Holo() {
		return new Location(world, -11.540, 74.162, 5.527);
	}

	@Override
	public Location getEnderchest2Holo() {
		return new Location(world, -5.503, 74.162, 11.508);
	}

	@Override
	public Location getUpgradesHolo() {
		return new Location(world, 9.535, 75.074, -2.515);
	}

	@Override
	public Location getPassHolo() {
		return new Location(world, 9.590, 75.074, 1.537);
	}

	@Override
	public Location getPrestigeHolo() {
		return new Location(world, -9.513, 75.074, 0.005);
	}

	@Override
	public Location getLeaderboardHolo() {
		return new Location(world, 3.453, 76.414, -10.627);
	}

	@Override
	public Location getGuildLeaderboardHolo() {
		return new Location(world, -3.337, 76.414, -10.606);
	}

	@Override
	public Location getKeeperHolo() {
		return new Location(world, 5.495, 75.303, 8.493);
	}

	@Override
	public Location getPitSimCrate() {
		return new Location(world, -1.5, 75, 9.5);
	}

	@Override
	public Location getVoteCrate() {
		return new Location(world, 1.5, 75, 9.5);
	}
}
