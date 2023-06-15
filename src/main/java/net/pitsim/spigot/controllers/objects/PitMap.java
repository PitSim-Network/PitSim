package net.pitsim.spigot.controllers.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class PitMap {
	public World world;
	public int rotationDays;

	public PitMap(String worldName, int rotationDays) {
		world = Bukkit.getWorld(worldName);
		this.rotationDays = rotationDays;
	}

	public abstract Location getSpawn();

	public abstract Location getFromDarkzoneSpawn();

	public abstract Location getNonSpawn();

	public abstract Location getMid();

	public abstract Location getPerksNPCSpawn();

	public abstract Location getPrestigeNPCSpawn();

	public abstract Location getKyroNPCSpawn();

	public abstract Location getWijiNPCSpawn();

	public abstract Location getSplkNPCSpawn();

	public abstract Location getStatsNPCSpawn();

	public abstract Location getKeeperNPCSpawn();

	public abstract Location getKitsNPCSpawn();

	public abstract Location getPassNPCSpawn();

	public abstract Location getStandAlonePortalRespawn();

	public abstract Location getWelcomeHolo();

	public abstract Location getMysticWellHolo();

	public abstract Location getKitsHolo();

	public abstract Location getEnderchest1Holo();

	public abstract Location getEnderchest2Holo();

	public abstract Location getUpgradesHolo();

	public abstract Location getPassHolo();

	public abstract Location getPrestigeHolo();

	public abstract Location getLeaderboardHolo();

	public abstract Location getGuildLeaderboardHolo();

	public abstract Location getKeeperHolo();

	public abstract Location getPitSimCrate();

	public abstract Location getVoteCrate();

	public double getY() {
		return getMid().getY();
	}
}
