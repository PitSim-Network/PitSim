package dev.kyro.pitsim.controllers.objects;

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

	public abstract int getTeleportAdd();

	public abstract int getTeleportY();

	public abstract String getOpenSchematic();

	public abstract String getClosedSchematic();

	public abstract Location getSchematicPaste();

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

	public double getY() {
		return getMid().getY();
	}
}
