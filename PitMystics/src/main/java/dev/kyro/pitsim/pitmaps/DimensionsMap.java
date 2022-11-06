package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;
import org.bukkit.World;

public class DimensionsMap extends PitMap {
	public DimensionsMap(String... worldNames) {
		super(worldNames);
	}

	@Override
	public Location getSpawn(World world) {
		if(!lobbies.contains(world) || (!MapManager.multiLobbies && world != MapManager.currentMap.firstLobby))
			return getSpawn(lobbies.get(0));
		return new Location(world, 0.5, 88, -5.5, 0, 0);
	}

//	TODO: chaos
	@Override
	public Location getNonSpawn(World world) {
		Location spawn = new Location(world, 0.5, 86, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 6 - 3));
		spawn.setZ(spawn.getZ() + (Math.random() * 6 - 3));
		return spawn;
	}

	@Override
	public int getTeleportAdd() { return -3; }

	@Override
	public int getTeleportY() { return 44;}

	@Override
	public String getOpenSchematic() { return "plugins/WorldEdit/schematics/map2DoorOpen.schematic"; }

	@Override
	public String getClosedSchematic() { return "plugins/WorldEdit/schematics/map2DoorClosed.schematic"; }

	@Override
	public Location getSchematicPaste(World world) { return new Location(world, 64, 44, 4); }

	@Override
	public Location getMid(World world) {
		return new Location(world, 0.5, 38, 0.5);
	}

	@Override
	public Location getUpgradeNPCSpawn(World world) {
		return new Location(world, -7.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn(World world) {
		return new Location(world, 9.5, 88, 0.5, 90, 0);
	}

	@Override
	public Location getKyroNPCSpawn(World world) {
		return new Location(world, -6.5, 89, 9.5, 180, 11);
	}

	@Override
	public Location getWijiNPCSpawn(World world) {
		return new Location(world, 3.5, 89, 9.5, 180, 10);
	}

	@Override
	public Location getSplkNPCSpawn(World world) {
		return new Location(world, -7.5, 89, 9.5, 45, 0);
	}

	@Override
	public Location getVnxNPCSpawn(World world) {
		return new Location(world, -1.5, 88, 10.5, 180, 0);
	}

	@Override
	public Location getKeeperNPCSpawn(World world)  {
		return new Location(world, 4.5, 88, 7.5, 150, 0);
	}

//	TODO: Add
	@Override
	public Location getKitNPCSpawn(World world) {
		return null;
	}
}
