package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PrestigeGUI;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PrestigeNPC extends PitNPC {

	public PrestigeNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getPrestigeNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnVillagerNPC(" ", location);
	}

	@Override
	public void onClick(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Tutorial tutorial = pitPlayer.tutorial;
		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.PRESTIGE)) {

			tutorial.sendMessage(" ", 10);
			tutorial.sendMessage(" ", 20);
			tutorial.completeObjective(TutorialObjective.PRESTIGE, 2 * 20);

			return;
		}

		PrestigeGUI prestigeGUI = new PrestigeGUI(player);
		prestigeGUI.open();
	}
}
