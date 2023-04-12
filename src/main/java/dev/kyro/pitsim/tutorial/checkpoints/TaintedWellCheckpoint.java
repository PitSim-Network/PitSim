package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class TaintedWellCheckpoint extends NPCCheckpoint {
	public TaintedWellCheckpoint() {
		super(TutorialObjective.TAINTED_WELL, new Location(MapManager.getDarkzone(),
				189.5, 92, -105.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("You can now access the Tainted Well", 0);
		tutorial.sendMessage("This is a special area where you can get special items", 20);
		tutorial.sendMessage("You can access the Tainted Well by typing &b/tw", 40);
		tutorial.sendMessage("You can also access the Tainted Well by clicking the &bTainted Well &7item in your inventory", 60);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.sendMessage("You have accessed the Tainted Well", 0);
		tutorial.sendMessage("You can now access the Tainted Well by typing &b/tw", 20);
		tutorial.sendMessage("You can also access the Tainted Well by clicking the &bTainted Well &7item in your inventory", 40);
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
	public boolean canSatisfy(Tutorial tutorial) {
		return true;
	}
}
