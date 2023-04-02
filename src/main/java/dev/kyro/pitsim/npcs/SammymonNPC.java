package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class SammymonNPC extends PitNPC {

	public SammymonNPC(List<World> worlds) {
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
		spawnPlayerNPC("&9Sammymon", "Sammymon", location, true);
	}

	@Override
	public void onClick(Player player) {}
}
