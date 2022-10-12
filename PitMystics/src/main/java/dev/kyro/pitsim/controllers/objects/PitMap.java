package dev.kyro.pitsim.controllers.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class PitMap {
	public World world;

	public PitMap(String worldName) {
		world = Bukkit.getWorld(worldName);

	}

	public abstract int getTeleportAdd();

	public abstract int getTeleportY();

	public abstract String getOpenSchematic();

	public abstract String getClosedSchematic();

	public abstract Location getSchematicPaste();

	public abstract Location getSpawn();

	public abstract Location getNonSpawn();

	public abstract Location getMid();

	public abstract Location getUpgradeNPCSpawn();

	public abstract Location getPrestigeNPCSpawn();

	public abstract Location getKyroNPCSpawn();

	public abstract Location getWijiNPCSpawn();

	public abstract Location getSplkNPCSpawn();

	public abstract Location getVnxNPCSpawn();

	public abstract Location getKeeperNPC();

	public double getY() {
		return getMid().getY();
	}

}
