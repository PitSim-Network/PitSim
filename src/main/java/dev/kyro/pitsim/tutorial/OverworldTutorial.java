package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class OverworldTutorial extends Tutorial {

	public OverworldTutorial(PitPlayer pitPlayer) {
		super(pitPlayer.overworldTutorialData, pitPlayer);
	}

	@Deprecated
	public OverworldTutorial(FileConfiguration playerData) {
		super(playerData);
	}

	@Override
	public Class<? extends Tutorial> getTutorialClass() {
		return OverworldTutorial.class;
	}

	@Override
	public void sendStartMessages() {
		sendMessage("&eHello! Welcome to &6&lPit&e&lSim&e!", 0);
		sendMessage("&eBefore you get started, we need to cover some basics.", 20 * 2);
		sendMessage("&eInteract with various NPCs around spawn to learn about how to play", 20 * 6);
	}

	@Override
	public int getStartTicks() {
		return 20 * 4;
	}

	@Override
	public void sendCompletionMessages() {
		sendMessage("&eIf you forget any of the information, each NPC has a help menu in the bottom right corner.", 90);
		sendMessage("&eYou can also join our discord server at &f&ndiscord.pitsim.net &efor more help.", 150);
		sendMessage("&eWith that being said, enjoy the server!", 210);
	}

	@Override
	public int getCompletionTicks() {
		return 210;
	}

	@Override
	public void onObjectiveComplete(TutorialObjective objective) {

	}

	@Override
	public boolean isActive() {
		return pitPlayer.prestige <= 1 && data.completedObjectives.size() < getObjectiveSize() && PitSim.status.isOverworld();
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onTutorialEnd() {

	}

	@Override
	public String getProceedMessage() {
		return null;
	}
}
