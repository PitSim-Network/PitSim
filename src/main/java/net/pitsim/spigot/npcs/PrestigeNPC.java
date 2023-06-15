package net.pitsim.spigot.npcs;

import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitNPC;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.inventories.PrestigeGUI;
import net.pitsim.spigot.tutorial.Tutorial;
import net.pitsim.spigot.tutorial.TutorialObjective;
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
		return MapManager.currentMap.getPrestigeNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnVillagerNPC(" ", location);
	}

	@Override
	public void onClick(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Tutorial tutorial = pitPlayer.overworldTutorial;
		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.PRESTIGE)) {

			tutorial.sendMessage("&b&lPRESTIGE: &eHave you reached &7[&b&l120&7] &eand can't level up anymore?", 0);
			tutorial.sendMessage("&b&lPRESTIGE: &eYou might finally be ready to prestige then!", 20 * 4);
			tutorial.sendMessage("&b&lPRESTIGE: &ePrestiging resets you to &9[&71&9]&e, but allows you unlock new upgrades with renown! Click on me to learn more!", 20 * 7);
			tutorial.completeObjective(TutorialObjective.PRESTIGE, 20 * 12);

			return;
		}

		PrestigeGUI prestigeGUI = new PrestigeGUI(player);
		prestigeGUI.open();
	}
}
