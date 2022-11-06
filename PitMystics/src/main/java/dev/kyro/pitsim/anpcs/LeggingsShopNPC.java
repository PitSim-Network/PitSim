package dev.kyro.pitsim.anpcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.LeggingsGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class LeggingsShopNPC extends PitNPC {

	public LeggingsShopNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 186, 91, -103, -40, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&b&lARMOR TRADER", "Merchant", location);
	}

	@Override
	public void onClick(Player player) {
		LeggingsGUI leggingsGUI = new LeggingsGUI(player);
		leggingsGUI.open();
	}
}
