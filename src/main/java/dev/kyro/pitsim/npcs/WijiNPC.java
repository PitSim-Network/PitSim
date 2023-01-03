package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.List;

public class WijiNPC extends PitNPC {

	public WijiNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getWijiNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		String skinName = "wiji1";
		if(LocalDate.now().isBefore(LocalDate.parse("2023-01-15"))) skinName = "Revernal";
		spawnPlayerNPC("&9wiji1", skinName, location, true);
	}

	@Override
	public void onClick(Player player) {}
}
