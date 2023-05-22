package dev.kyro.pitsim.controllers;

import dev.kyro.arcticguilds.Guild;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OutpostManager implements Listener {
	public static double CONTROL_INCREMENT = 0.5;

	public static Guild controllingGuild;
	public static boolean isActive;
	public static double percentControlled = 0;

	public static long lastContestingNotification = 0;

	public OutpostManager() {
		if(PitSim.status.isDarkzone()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					List<Guild> guildsInOutpost = getGuildsInOutpost();
					List<Guild> enemyGuilds = new ArrayList<>(guildsInOutpost);
					boolean activeGuildPresent = enemyGuilds.remove(controllingGuild);

//				A guild controls the outpost and no one is capturing it
					if(enemyGuilds.isEmpty() && controllingGuild != null) {
						if(isActive) {
							increaseControl();
						} else {
							decreaseControl(null);
						}
					} else if(!activeGuildPresent && enemyGuilds.size() == 1) {
						decreaseControl(enemyGuilds.remove(0));
					} else if(controllingGuild == null && guildsInOutpost.size() == 1) {
						setControllingGuild(guildsInOutpost.remove(0));
					}
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

			new BukkitRunnable() {
				@Override
				public void run() {
					if(isActive) {
//						TODO: give the controlling guild getGoldAmount() gold (use Formatter.formatGoldFull() to display it)
					}
				}
			}.runTaskTimer(PitSim.INSTANCE, 20 * 60 * 10, 20 * 60 * 10);
		}
	}

	public static void increaseControl() {
		if(percentControlled == 100) return;
		if(percentControlled + CONTROL_INCREMENT >= 100) {
			percentControlled = 100;
			setActive(true);
			lastContestingNotification = 0L;
			String message = "&3&lOUTPOST! " + controllingGuild.color + controllingGuild.name + " &7has captured the outpost!";
			Misc.broadcast(message);
		} else {
			percentControlled += CONTROL_INCREMENT;
		}
	}

	public static void decreaseControl(Guild capturingGuild) {
		if(percentControlled == 0) return;
		if(percentControlled - CONTROL_INCREMENT <= 0) {
			percentControlled = 0;
			if(isActive) {
				String message = "&3&lOUTPOST! " + controllingGuild.color + controllingGuild.name + " &7is losing control of the outpost!";
				Misc.broadcast(message);
			}
			setControllingGuild(null);
			setActive(false);
		} else {
			percentControlled -= CONTROL_INCREMENT;
			if(isActive && lastContestingNotification + 20 * 10 < PitSim.currentTick) {
				lastContestingNotification = PitSim.currentTick;
				sendGuildMessage(controllingGuild.uuid, "&eThe Outpost is being captured by " + capturingGuild.color + capturingGuild.name + "&e!");
			}
		}
	}

	public static void setControllingGuild(Guild guild) {
		controllingGuild = guild;
		sendOutpostData();
	}

	public static void setActive(boolean active) {
		isActive = active;
		sendOutpostData();
	}

	public static void sendOutpostData() {
		PluginMessage message = new PluginMessage().writeString("OUTPOST DATA");
		message.writeString(controllingGuild == null ? "null" : controllingGuild.uuid.toString());
		message.writeBoolean(isActive);
		message.send();
	}

	public static List<Guild> getGuildsInOutpost() {
//		TODO: return all the guilds in the outpost
		return new ArrayList<>();
	}

	public static void sendGuildMessage(UUID guildUUID, String message) {
		PluginMessage pluginMessage = new PluginMessage().writeString("GUILD MESSAGE");
		pluginMessage.writeString(guildUUID.toString());
		pluginMessage.writeString(message);
		pluginMessage.send();
	}

	public static int getOutpostFreshIncrease() {
		return 50;
	}

	public static int getGoldAmount() {
		return 50_000;
	}
}
