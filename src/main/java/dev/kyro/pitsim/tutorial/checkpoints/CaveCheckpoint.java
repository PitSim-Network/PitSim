package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.Midpoint;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

public class CaveCheckpoint extends NPCCheckpoint {
	public CaveCheckpoint() {
		super(TutorialObjective.MONSTER_CAVES, new Location(MapManager.getDarkzone(),
				276.5, 91, -117.5, 13, 0), Midpoint.ENTRANCE, Midpoint.SPAWN1, Midpoint.SPAWN2, Midpoint.EXIT, Midpoint.PATH1, Midpoint.PATH2);
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eWelcome to the &cMonster Caves&e!", 0);
		tutorial.sendMessage("&eHere you can fight monsters to gain &fSouls&e.", 60);
		tutorial.sendMessage("&eThe caves consist of &410 Levels&e, each containing a unique &4Boss Fight&e.", 120);
		tutorial.sendMessage("&eUse the &fSouls &eearned here back in &5Spawn &eto progress through the different &4Levels&e.", 180);
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
