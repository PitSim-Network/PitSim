package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
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
		return MapManager.currentMap.getKeeperNPCSpawn(world);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&2&lTHE KEEPER", "googasesportsog", location, false);
	}

	@Override
	public void onClick(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Tutorial tutorial = pitPlayer.tutorial;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.KEEPER) && !tutorial.isInObjective) {

			String playerName = Misc.getRankColor(player.getUniqueId()) + player.getDisplayName();
			tutorial.sendMessage("&2&lKEEPER: &eHi there " + playerName + "&e! Can't find any players or can't streak as efficiently?", 0);
			tutorial.sendMessage("&2&lKEEPER: &e I may be able to help you with that, as long as there's enough players online!", 20 * 4);
			tutorial.sendMessage("&2&lKEEPER: &eClick on me again and I'll take you to the other lobby if able to!", 20 * 8);
			tutorial.completeObjective(TutorialObjective.KEEPER, 20 * 12);

			return;
		}

		MapManager.changeLobbies(player);
	}
}
