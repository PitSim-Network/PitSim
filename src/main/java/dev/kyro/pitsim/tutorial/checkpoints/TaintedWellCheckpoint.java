package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class TaintedWellCheckpoint extends NPCCheckpoint {
	public TaintedWellCheckpoint() {
		super(TutorialObjective.TAINTED_WELL, new Location(MapManager.getDarkzone(),
				189.5, 92, -105.5, 13, 0), 60);
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {

	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {

	}
}
