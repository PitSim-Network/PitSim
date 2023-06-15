package net.pitsim.pitsim.npcs;

import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitNPC;
import net.pitsim.pitsim.inventories.stats.StatGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class StatsNPC extends PitNPC {

	public StatsNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getStatsNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&e&lLB AND STATS", "Whyplay", location, false);
	}

	@Override
	public void onClick(Player player) {
		StatGUI statGUI = new StatGUI(player);
		statGUI.open();
	}
}
