package dev.kyro.pitsim.controllers.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public abstract class PitMap {
	public List<World> lobbies = new ArrayList<>();
	public World firstLobby;

	public PitMap(String... worldNames) {
		firstLobby = Bukkit.getWorld(worldNames[0]);
		for(String worldName : worldNames) lobbies.add(Bukkit.getWorld(worldName));
	}

	public abstract Location getSpawn(World world);
	public abstract Location getNonSpawn(World world);
	public abstract Location getMid(World world);
	public abstract Location getUpgradeNPCSpawn(World world);
	public abstract Location getPrestigeNPCSpawn(World world);
	public abstract Location getKyroNPCSpawn(World world);
	public abstract Location getWijiNPCSpawn(World world);
	public abstract Location getVnxNPCSpawn(World world);

	public double getY(World world) {
		return getMid(world).getY();
	}

	public int getLobbyIndex(World world) {
		for(int i = 0; i < lobbies.size(); i++) {
			World testWorld = lobbies.get(i);
			if(testWorld == world) return i;
		}
		return -1;
	}
}
