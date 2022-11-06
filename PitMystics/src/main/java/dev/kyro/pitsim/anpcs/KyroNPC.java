package dev.kyro.pitsim.anpcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class KyroNPC extends PitNPC {

	public KyroNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getKyroNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&9KyroKrypt", "KyroKrypt", location);
	}

	@Override
	public void onClick(Player player) {}
}
