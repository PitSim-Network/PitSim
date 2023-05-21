package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.boosters.ChaosBooster;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;

public class DimensionsMap extends PitMap {
	public DimensionsMap(String worldName, int rotationDays) {
		super(worldName, rotationDays);
	}

	@Override
	public Location getSpawn() {
		return new Location(world, 0.5, 55, 10.5, 180, 0);
	}

	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0.5, 53, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 8 - 4));
		spawn.setZ(spawn.getZ() + (Math.random() * 8 - 4));

		if(ChaosBooster.INSTANCE.isActive()) {
			spawn.add(0, -15, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -10, 0);
		}
		return spawn;
	}

	@Override
	public Location getFromDarkzoneSpawn() {
		return new Location(world, -75.5, 26, 0, -90, 0);
	}

	@Override
	public Location getMid() {
		return new Location(world, 0.5, 22, 0.5);
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, 12.5, 55, -1.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -11.5, 55, 0.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, -4.5, 58, 14.5, 180, 0);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, 5.5, 58, 15.5, 160, 0);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, 6.5, 58, -13.5, 35, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, 2.5, 55, -11.5, 0, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, -7.5, 55, -10.5, -25, 0);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(world, -0.5, 26, 77, -180, 0);
	}

	@Override
	public Location getWelcomeHolo() {
		return new Location(world, 0.598, 58.6, 5.549);
	}

	@Override
	public Location getMysticWellHolo() {
		return new Location(world, 0.509, 57.400, 14.3);
	}

	@Override
	public Location getKitsHolo() {
		return new Location(world, -1, 57.1, 14);
	}

	@Override
	public Location getEnderchest1Holo() {
		return new Location(world, 4.472, 57.800, 13.599);
	}

	@Override
	public Location getEnderchest2Holo() {
		return new Location(world, -3.472, 57.800, 13.599);
	}

	@Override
	public Location getUpgradesHolo() {
		return new Location(world, 12.474, 57.138, -1.573);
	}

	@Override
	public Location getPassHolo() {
		return new Location(world, 12.474, 57.138, 2.573);
	}

	@Override
	public Location getPrestigeHolo() {
		return new Location(world, -11.5, 57.164, 0.423);
	}

	@Override
	public Location getLeaderboardHolo() {
		return new Location(world, -0.700, 60.0, -12.184);
	}

	@Override
	public Location getGuildLeaderboardHolo() {
		return new Location(world, -4.487, 60.0, -11.779);
	}

	@Override
	public Location getKeeperHolo() {
		return new Location(world, -7.511, 57.162, -10.581);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 12.5, 55, 2.5, 90, 0);
	}

	//	TODO: Add
	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, -1, 56, 14, -165, 0);
	}

	@Override
	public Location getPitSimCrate() {
		return new Location(world, -11.5, 57, -4.5);
	}

	@Override
	public Location getVoteCrate() {
		return new Location(world, -11.5, 57, 5.5);
	}
}
