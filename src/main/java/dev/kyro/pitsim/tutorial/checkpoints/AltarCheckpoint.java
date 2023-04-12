package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class AltarCheckpoint extends NPCCheckpoint {
	public AltarCheckpoint() {
		super(TutorialObjective.ALTAR, new Location(MapManager.getDarkzone(),
				218.5, 91, -88.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("You can now access the Altar", 0);
		tutorial.sendMessage("This is a special area where you can get special items", 20);
		tutorial.sendMessage("You can access the Altar by typing &b/altar", 40);
		tutorial.sendMessage("You can also access the Altar by clicking the &bAltar &7item in your inventory", 60);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.sendMessage("You have accessed the Altar", 0);
		tutorial.sendMessage("You can now access the Altar by typing &b/altar", 20);
		tutorial.sendMessage("You can also access the Altar by clicking the &bAltar &7item in your inventory", 40);
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
