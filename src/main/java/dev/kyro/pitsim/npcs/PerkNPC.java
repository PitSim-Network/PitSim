package dev.kyro.pitsim.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PerkGUI;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PerkNPC extends PitNPC {

	public PerkNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getPerksNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		spawnVillagerNPC(" ", location);
	}

	@Override
	public void onClick(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.isOnMega()) {
			AOutput.error(player, "&c&lERROR!&7 You cannot use this command while on a megastreak!");
			return;
		}

		Tutorial tutorial = pitPlayer.overworldTutorial;
		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.PERKS)) {

			String playerName = Misc.getRankColor(player.getUniqueId()) + player.getDisplayName();
			tutorial.sendMessage("&a&lPERKS: &eOh, hello there " + playerName + "&e! Do you need a boost to help you out?", 0);
			tutorial.sendMessage("&a&lPERKS: &eWell you came to the right guy! With me, you can set up perks, killstreaks, & mega streaks, which will up your game!", 20 * 3);
			tutorial.sendMessage("&a&lPERKS: &eUse these upgrades to be the best at whatever you do! With me, the possibilities are endless!", 20 * 8);
			tutorial.completeObjective(TutorialObjective.PERKS, 20 * 12);

			return;
		}

		PerkGUI perkGUI = new PerkGUI(player);
		perkGUI.open();
	}
}
