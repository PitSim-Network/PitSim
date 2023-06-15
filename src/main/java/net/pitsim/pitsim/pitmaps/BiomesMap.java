package net.pitsim.pitsim.pitmaps;

import net.pitsim.pitsim.boosters.ChaosBooster;
import net.pitsim.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;

public class BiomesMap extends PitMap {
	public BiomesMap(String worldName, int rotationDays) {
		super(worldName, rotationDays);
	}

	public static Location mid = new Location(null, 0.5, 70, 0.5);

	@Override
	public Location getSpawn() {
		return new Location(world, 0.5, 88, 8.5, -180, 0);
	}

	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0.5, 86, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 6 - 3));
		spawn.setZ(spawn.getZ() + (Math.random() * 6 - 3));

		if(ChaosBooster.INSTANCE.isActive()) {
			spawn.add(0, -10, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -5, 0);
		}
		return spawn;
	}

	@Override
	public Location getFromDarkzoneSpawn() {
		return new Location(world, -67, 72, 3, -90, 0);
	}

	@Override
	public Location getMid() {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, 10.5, 88, 3.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -12.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, 7.5, 92, -8.5, 22.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, 0.5, 92, -11.5, 31, 10);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, 8.5, 90, -7.5, 45, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, 2.5, 88, -8.5, 10, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, -2.5, 88, -10, 10, 0);
	}

	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, -2.5, 90, 12.5, -145, 15);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 10.5, 88, 5.5, 90, 0);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(null, -53.5, 73, 0.5, -90, 0);
	}

	@Override
	public Location getWelcomeHolo() {
		return new Location(world, 0.558, 91.6, 4.915);
	}

	@Override
	public Location getMysticWellHolo() {
		return new Location(world, 0.462, 90.8, 12.209);
	}

	@Override
	public Location getKitsHolo() {
		return new Location(world, -2.500, 91, 12.500);
	}

	@Override
	public Location getEnderchest1Holo() {
		return new Location(world, 4.499, 89.5, 11.472);
	}

	@Override
	public Location getEnderchest2Holo() {
		return new Location(world, -3.512, 89.5, 11.273);
	}

	@Override
	public Location getUpgradesHolo() {
		return new Location(world, 10.474, 90.1, 3.573);
	}

	@Override
	public Location getPassHolo() {
		return new Location(world, 10.474, 90.1, 5.573);
	}

	@Override
	public Location getPrestigeHolo() {
		return new Location(world, -9.700, 90.1, -1.423);
	}

	@Override
	public Location getLeaderboardHolo() {
		return new Location(world, 4.663, 93.8, -7.811);
	}

	@Override
	public Location getGuildLeaderboardHolo() {
		return new Location(world, 1.042, 93.8, -9.456);
	}

	@Override
	public Location getKeeperHolo() {
		return new Location(world, -2.511, 90.4, -9.981);
	}

	@Override
	public Location getPitSimCrate() {
		return new Location(world, -10.5, 90, 6.5);
	}

	@Override
	public Location getVoteCrate() {
		return new Location(world, -10.5, 90, 4.5);
	}
}
