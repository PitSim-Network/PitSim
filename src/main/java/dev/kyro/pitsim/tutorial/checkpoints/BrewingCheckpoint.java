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
		tutorial.sendMessage("Brewing is a special area where you can get special items", 0);
		tutorial.sendMessage("You can access the Brewing area by typing &b/brewing", 20);
		tutorial.sendMessage("You can also access the Brewing area by clicking the &bBrewing &7item in your inventory", 40);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.sendMessage("You have accessed the Brewing area", 0);
		tutorial.sendMessage("You can now access the Brewing area by typing &b/brewing", 20);
		tutorial.sendMessage("You can also access the Brewing area by clicking the &bBrewing &7item in your inventory", 40);
	}

	@Override
	public int getEngageDelay() {
		return 60;
	}

	@Override
	public int getSatisfyDelay() {
		return 60;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return true;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		return true;
	}
}
