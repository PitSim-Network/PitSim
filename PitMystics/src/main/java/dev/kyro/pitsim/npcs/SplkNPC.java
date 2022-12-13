package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class SplkNPC extends PitNPC {

	public SplkNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getSplkNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&9Splkpig", "Splkpig", location, true);
	}

	@Override
	public void onClick(Player player) {}
}
