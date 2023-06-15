package net.pitsim.spigot.controllers;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.enums.DiscordLogChannel;
import net.pitsim.spigot.misc.Misc;
import litebans.api.Entry;
import litebans.api.Events;

import java.util.Objects;

public class LitebansManager {

	public static void init() {
		if(!PitSim.serverName.equals("pitsim-1") && !PitSim.serverName.equals("pitsimdev-1")) return;

		Events.get().register(new Events.Listener() {
			@Override
			public void entryAdded(Entry entry) {
				if(entry.getType().equals("ban")) {
					if(!Objects.equals(entry.getExecutorName(), "Console")) return;
					if(entry.getReason().contains("[Anticheat]")) return;

					String message = "```ID: " + entry.getId() + "\nPlayer: |\nReason: "
							+ entry.getReason() + "\nDuration: " + entry.getDurationString() + "```," + entry.getUuid();

					Misc.logToDiscord(DiscordLogChannel.BAN_LOG_CHANNEL, message);
				}
			}
		});
	}

}
