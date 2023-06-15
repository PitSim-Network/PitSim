package net.pitsim.pitsim.npcs;

import net.pitsim.pitsim.adarkzone.FastTravelGUI;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class FastTravelNPC extends PitNPC {

	public FastTravelNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 182.5, 91, -88.5, 170, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&f&lFAST TRAVEL", "Mailman", location, false);
	}

	@Override
	public void onClick(Player player) {
		FastTravelGUI gui = new FastTravelGUI(player);
		gui.open();
	}
}
