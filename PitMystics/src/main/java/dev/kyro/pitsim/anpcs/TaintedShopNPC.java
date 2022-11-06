package dev.kyro.pitsim.anpcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.TaintedGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class TaintedShopNPC extends PitNPC {

	public TaintedShopNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 214, 91, -113, 25, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("", "dominorift", location);
	}

	@Override
	public void onClick(Player player) {
		TaintedGUI taintedGUI = new TaintedGUI(player);
		taintedGUI.open();
	}
}
