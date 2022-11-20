package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.configuration.file.FileConfiguration;

public class PitsimTutorial extends Tutorial {

	PitPlayer pitPlayer;

	public PitsimTutorial(PitPlayer pitPlayer, FileConfiguration playerData) {
		super(pitPlayer, playerData, "pitsim", false);

		this.pitPlayer = pitPlayer;
	}

	@Override
	public void sendStartMessages() {
		sendMessage("&eHello! Welcome to &6&lPit&e&lSim&e!", 0);
		sendMessage("&eBefore you get started, we need to cover some basics.", 20 * 2);
		sendMessage("&eInteract with various NPCs around spawn to learn about how to play", 20 * 6);
	}

	@Override
	public void sendEndMessages() {
		sendMessage("&eIf you forget any of the information, each NPC has a help menu in the bottom right corner.", 90);
		sendMessage("&eYou can also join our discord server at &f&ndiscord.pitsim.net &efor more help.", 150);
		sendMessage("&eWith that being said, enjoy the server!", 210);
	}

	@Override
	public boolean canBeActive() {
		return pitPlayer.prestige <= 1;
	}

	@Override
	public String getBossBarDisplay(int completedObjectives) {
		return null;
	}
}
