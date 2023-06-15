package net.pitsim.pitsim.npcs;

import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitNPC;
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
		return MapManager.currentMap.getKyroNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&9KyroKrypt", "kyrokrypt", location, true);
	}

	@Override
	public void onClick(Player player) {}
}
