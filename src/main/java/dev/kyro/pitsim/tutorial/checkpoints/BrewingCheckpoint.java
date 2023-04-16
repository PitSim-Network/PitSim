package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class BrewingCheckpoint extends NPCCheckpoint {
	public BrewingCheckpoint() {
		super(TutorialObjective.BREWING, new Location(MapManager.getDarkzone(),
				218, 91, -99.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eHere is the &d&lBrewing Area&e!", 0);
		tutorial.sendMessage("&eHere you can brew &dPotions &eusing &cMob Drops&e.", 60);
		tutorial.sendMessage("&ePotions currently have &f10 Unique Effects &efor use in both the &aOverworld &e and &5Darkzone&e.", 120);
		tutorial.sendMessage("&eThe potion system will soon be reworked, so hold tight.", 180);
		tutorial.sendMessage("&eInteract with me to continue.", 240);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {

	}

	@Override
	public int getEngageDelay() {
		return 240;
	}

	@Override
	public int getSatisfyDelay() {
		return 0;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return true;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		return true;
	}

	@Override
	public void onCheckPointDisengage(Tutorial tutorial) {

	}
}
