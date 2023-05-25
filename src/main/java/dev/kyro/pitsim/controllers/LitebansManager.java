package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.libs.discord.DiscordWebhook;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PrivateInfo;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Objects;

public class LitebansManager {

	public static void init() {
		if(!PitSim.serverName.equals("pitsim-1") && !PitSim.serverName.equals("pitsimdev-1")) return;

		Events.get().register(new Events.Listener() {
			@Override
			public void entryAdded(Entry entry) {
				if(entry.getType().equals("ban")) {
					if(!Objects.equals(entry.getExecutorName(), "Console")) return;

					String message = "```ID: " + entry.getId() + "\011Player: " + entry.getRemovedByName() + "\012Reason: \012"
							+ entry.getReason() + "\012Duration: " + entry.getDurationString() + "```";

					Misc.alertDiscord(message);
					sendBan(message);
				}
			}
		});
	}

	public static void sendBan(String message) {
		DiscordWebhook discordWebhook = new DiscordWebhook(PrivateInfo.BANS_WEBHOOK);
		discordWebhook.setContent(message);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					discordWebhook.execute();
				} catch(IOException exception) {
					exception.printStackTrace();
				}
			}
		}.runTaskAsynchronously(PitSim.INSTANCE);
	}

}
