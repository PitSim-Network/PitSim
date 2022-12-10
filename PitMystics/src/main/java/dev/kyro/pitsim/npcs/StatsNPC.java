package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.stats.StatGUI;
import dev.kyro.pitsim.misc.Misc;
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
		return MapManager.currentMap.getVnxNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&e&lLB AND STATS", Misc.fetchUsernameFromMojang("e913fd01-e84e-4c6e-ad5b-7419a12de481"), location, false);
	}

	@Override
	public void onClick(Player player) {
		StatGUI statGUI = new StatGUI(player);
		statGUI.open();
	}
}
