package dev.kyro.pitsim.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LobbySwitchManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.KeeperGUI;
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
		return MapManager.currentMap.getKeeperNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&2&lTHE KEEPER", "googasesportsog", location);
	}

	@Override
	public void onClick(Player player) {
		if(LobbySwitchManager.recentlyJoined.contains(player)) {
			AOutput.error(player, "&c&lNOPE! &7You cannot use this command for 5 seconds after joining!");
			return;
		}

		KeeperGUI keeperGUI = new KeeperGUI(player);
		keeperGUI.open();
	}
}
