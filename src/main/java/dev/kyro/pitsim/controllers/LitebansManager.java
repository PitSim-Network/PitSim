package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.DiscordLogChannel;
import dev.kyro.pitsim.misc.Misc;
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

					String message = "```ID: " + entry.getId() + "\nPlayer: |\nReason: "
							+ entry.getReason() + "\nDuration: " + entry.getDurationString() + "```," + entry.getUuid();

					Misc.logToDiscord(DiscordLogChannel.BAN_LOG_CHANNEL, message);
				}
			}
		});
	}

}
