package dev.kyro.pitsim.tutorial;

import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;

public class MessageManager {

	private static MessageBlocker blocker;

	public static MessageBlocker getBlocker() {
		if(blocker == null) blocker = MessageBlockerAPI.builder(PitSim.INSTANCE).build();
		return blocker;
	}

	public static void sendTutorialMessage(Player player, TutorialMessage message) {
		getBlocker().announce(player, message.message);

		AOutput.send(player, message.message);
	}

	public static void blockPlayer(Player player) {
		getBlocker().blockPlayer(player);
	}

	public static void unblockPlayer(Player player) {
		getBlocker().unblockPlayer(player);
	}
}
