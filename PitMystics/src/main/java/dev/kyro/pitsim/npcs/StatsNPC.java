package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.stats.StatGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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
		spawnPlayerNPC("&e&lLB AND STATS", Bukkit.getOfflinePlayer(UUID.fromString("e913fd01-e84e-4c6e-ad5b-7419a12de481")).getName(), location);
	}

	@Override
	public void onClick(Player player) {
		StatGUI statGUI = new StatGUI(player);
		statGUI.open();
	}
}
