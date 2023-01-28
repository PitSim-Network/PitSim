package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitMap;
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
		Location spawn = new Location(world, 0, 72  , 0);
		spawn.setX(spawn.getX() + (Math.random() * 4 - 2));
		spawn.setZ(spawn.getZ() + (Math.random() * 4 - 2));

		Booster booster = BoosterManager.getBooster("chaos");
		if(booster.isActive()) {
			spawn.add(0, -10, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -5, 0);
		}
		return spawn;
	}

	@Override
	public Location getDarkzoneJoinSpawn() {
		return new Location(world, -36, 31, 1, -90, 0);
	}

	@Override
	public int getTeleportAdd() {return 3;}

	@Override
	public int getTeleportY() {return 72;}

	@Override
	public String getOpenSchematic() {return "plugins/WorldEdit/schematics/doorOpen.schematic";}

	@Override
	public String getClosedSchematic() {return "plugins/WorldEdit/schematics/doorClosed.schematic";}

	@Override
	public Location getSchematicPaste() {return new Location(world, -67, 72, 3);}

	@Override
	public Location getMid() {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getPerksNPCSpawn() {
		return new Location(world, 7.5, 73, 0.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -11.5, 73, 2, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, -10.5, 73, -5.5, -45.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, -12.5, 73, -5.5, -55, 10);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, -12.5, 73, -3.5, -62, 0);
	}

	@Override
	public Location getStatsNPCSpawn() {
		return new Location(world, -2, 73, -7.5, 0, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, 3.5, 73, 10.5, 145, 0);
	}

	@Override
	public Location getKitsNPCSpawn() {
		return new Location(world, 7.5, 73, 9.5, 125, 0);
	}

	@Override
	public Location getPassNPCSpawn() {
		return new Location(world, 7.5, 73, 3.5, 90, 0);
	}

	@Override
	public Location getStandAlonePortalRespawn() {
		return new Location(null, -53.5, 73, 0.5, -90, 0);
	}
}
