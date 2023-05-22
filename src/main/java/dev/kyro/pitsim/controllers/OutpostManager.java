package dev.kyro.pitsim.controllers;

import dev.kyro.arcticguilds.Guild;
import dev.kyro.pitsim.PitSim;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OutpostManager implements Listener {
	public static double CONTROL_INCREMENT = 0.5;

//	TODO: load this variable on the spigot end from plugin messages by proxy
	public static Guild controllingGuild;
//	TODO: load this variable on the spigot end from plugin messages by proxy
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
						controllingGuild = guildsInOutpost.remove(0);
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
			isActive = true;
			lastContestingNotification = 0L;
//			TODO: Broadcast new capture to all pitsim servers
		} else {
			percentControlled += CONTROL_INCREMENT;
		}
	}

	public static void decreaseControl(Guild capturingGuild) {
		if(percentControlled == 0) return;
		if(percentControlled - CONTROL_INCREMENT <= 0) {
			percentControlled = 0;
			if(isActive) {
//				TODO: broadcast loss of control to all pitsim servers
			}
			controllingGuild = null;
			isActive = false;
		} else {
			percentControlled -= CONTROL_INCREMENT;
			if(isActive && lastContestingNotification + 20 * 10 < PitSim.currentTick) {
				lastContestingNotification = PitSim.currentTick;
//				TODO: inform controllingGuild that their guild is being captured by capturingGuild (capturingGuild won't ever be null)
			}
		}
	}

	public static List<Guild> getGuildsInOutpost() {
//		TODO: return all the guilds in the outpost
		return new ArrayList<>();
	}

	public static int getOutpostFreshIncrease() {
		return 50;
	}

	public static int getGoldAmount() {
		return 50_000;
	}
}
