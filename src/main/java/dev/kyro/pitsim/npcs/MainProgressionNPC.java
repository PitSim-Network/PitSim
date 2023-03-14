package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.adarkzone.progression.ProgressionGUI;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class MainProgressionNPC extends PitNPC {

	public MainProgressionNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 208.5, 91, -108.5, 76F, 3.5F);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&5&lDARKZONE SKILLS", "Revernal", location, true);
	}

	@Override
	public void onClick(Player player) {
		ProgressionGUI progressionGUI = new ProgressionGUI(player);
		progressionGUI.open();
	}
}
