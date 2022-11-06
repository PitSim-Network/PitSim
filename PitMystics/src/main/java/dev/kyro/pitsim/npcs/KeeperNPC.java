package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
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

			tutorial.sendMessage(" ", 10);
			tutorial.sendMessage(" ", 20);
			tutorial.completeObjective(TutorialObjective.KEEPER, 2 * 20);

			return;
		}

		MapManager.changeLobbies(player);
	}
}
