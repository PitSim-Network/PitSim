package net.pitsim.pitsim.misc;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.entity.Player;

public class Lang {
	public static Message COULD_NOT_FIND_PLAYER_WITH_NAME = new Message("&c&lERROR!&7 Could not find a player with that name");
	public static Message NO_PERMISSION = new Message("&c&lERROR!&7 You do not have permission for that");
	public static Message NOT_ENOUGH_SOULS = new Message("&c&lERROR!&7 You do not have enough souls");

	public static class Message {
		public String message;

		public Message(String message) {
			this.message = message;
		}

		public void send(Player player) {
			AOutput.send(player, message);
		}
	}
}
