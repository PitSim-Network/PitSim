package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;

public class DimensionsMap extends PitMap {
	public DimensionsMap(String worldName) {
		super(worldName);
	}

	@Override
	public Location getSpawn() {
		return new Location(world, 9.5, 55, 0.5, 90, 0);
	}

	//	TODO: chaos
	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0.5, 53, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 8 - 3));
		spawn.setZ(spawn.getZ() + (Math.random() * 8 - 3));
		return spawn;
	}

	@Override
	public Location getDarkzoneJoinSpawn() {
		return new Location(world, -0.5, 26, 77, -180, 0);
	}

	@Override
	public int getTeleportAdd() {return -3;}

	@Override
	public int getTeleportY() {return 44;}

	@Override
	public String getOpenSchematic() {return "plugins/WorldEdit/schematics/map2DoorOpen.schematic";}

	@Override
	public String getClosedSchematic() {return "plugins/WorldEdit/schematics/map2DoorClosed.schematic";}

	@Override
	public Location getSchematicPaste() {return new Location(world, 64, 44, 4);}

	@Override
	public Location getMid() {
		return new Location(world, 0.5, 22, 0.5);
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, -1.5, 55, -11.5, 0, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, 0.5, 55, 12.5, -180, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, -4.5, 58, 14.5, 180, 0);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, 5.5, 58, 14.5, 180, 0);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, -5.5, 58, -13.5, -25, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, -11.5, 55, 0.5, -90, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, -10.5, 55, 7.5, -120, 0);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(world, -0.5, 26, 77, -180, 0);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 2.5, 55, -11.5, 0, 0);
	}

	//	TODO: Add
	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, 15.5, 57, 2.5, 125, 0);
	}
}
