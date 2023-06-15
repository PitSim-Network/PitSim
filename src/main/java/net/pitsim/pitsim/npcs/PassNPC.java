package net.pitsim.pitsim.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.battlepass.inventories.PassGUI;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitNPC;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.tutorial.TutorialObjective;
import net.pitsim.pitsim.tutorial.Tutorial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PassNPC extends PitNPC {

	public PassNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getPassNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnVillagerNPC(" ", location);
	}

	@Override
	public void onClick(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.isOnMega() && !player.isOp()) {
			AOutput.error(player, "&c&lERROR!&7 You cannot use this while on a megastreak!");
			return;
		}

		Tutorial tutorial = pitPlayer.overworldTutorial;
		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.PASS)) {

			String playerName = Misc.getRankColor(player.getUniqueId()) + player.getDisplayName();
			tutorial.sendMessage("&3&lBATTLE PASS: &eWhat's up " + playerName + "&e! Want some free loot?", 0);
			tutorial.sendMessage("&3&lBATTLE PASS: &eHere you can participate in &6 Daily &eand &6Weekly Quests &ein exchange for rewards", 20 * 3);
			tutorial.sendMessage("&3&lBATTLE PASS: &eYou can also get more rewards by purchasing the &3Battle Pass &eat &f&nstore.pitsim.net", 20 * 8);
			tutorial.completeObjective(TutorialObjective.PASS, 20 * 12);

			return;
		}

		PassGUI passGUI = new PassGUI(player);
		passGUI.open();
	}
}
