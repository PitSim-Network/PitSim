package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class ProgressionCheckpoint extends NPCCheckpoint {
	public ProgressionCheckpoint() {
		super(TutorialObjective.PROGRESSION, new Location(MapManager.getDarkzone(),
				190.5, 91, -86.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("Progression is the main way to get better items", 0);
		tutorial.sendMessage("You can progress by completing objectives", 20);
		tutorial.sendMessage("You can see your objectives by typing &b/objectives", 40);
		tutorial.sendMessage("You can also see your objectives by clicking the &bObjectives &7item in your inventory", 60);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.sendMessage("You have completed an objective", 0);
		tutorial.sendMessage("You can see your objectives by typing &b/objectives", 20);
		tutorial.sendMessage("You can also see your objectives by clicking the &bObjectives &7item in your inventory", 40);
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
