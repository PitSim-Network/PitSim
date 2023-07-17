package net.pitsim.spigot.npcs;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitNPC;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.darkzone.progression.ProgressionManager;
import net.pitsim.spigot.darkzone.progression.SkillBranch;
import net.pitsim.spigot.darkzone.progression.skillbranches.BrewingBranch;
import net.pitsim.spigot.inventories.PotionMasterGUI;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PotionMasterNPC extends PitNPC {

	public PotionMasterNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 216.5, 91, -102.5, 25, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&d&lPOTIONS", "Wiizard", location, false);
	}

	@Override
	public void onClick(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!ProgressionManager.isUnlocked(pitPlayer, BrewingBranch.INSTANCE, SkillBranch.MajorUnlockPosition.FIRST_PATH)) {
			Sounds.NO.play(player);
			AOutput.error(player, "&cUnlock access to the Potion Master in the brewing section of the skill tree!");
			return;
		}

		PotionMasterGUI potionMasterGUI = new PotionMasterGUI(player);
		potionMasterGUI.open();
	}
}
