package dev.kyro.pitsim.anpcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class KeeperNPC extends PitNPC {

	public KeeperNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getKeeperNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&2&lTHE KEEPER", "googasesportsog", location);
	}

	@Override
	public void onClick(Player player) {
		MapManager.changeLobbies(player);
	}
}
