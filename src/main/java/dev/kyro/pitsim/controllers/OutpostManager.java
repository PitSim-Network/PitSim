package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OutpostManager implements Listener {
	public static double CONTROL_INCREMENT = 0.5;

	public static Guild controllingGuild;
	public static double percentControlled = 0;
	public static boolean isActive;

	public static long lastContestingNotification = 0;

	public OutpostManager() {
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
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
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
//				TODO: inform controllingGuild that their guild is being captured by capturingGuild (capturingGuild can be null)
			}
		}
	}

	public static List<Guild> getGuildsInOutpost() {
//		TODO: implement
//		should return the guild in the outpost
//		if there are multiple guilds in the outpost, it should return null
		return new ArrayList<>();
	}

//	TODO: Implement this in ArcticGuilds
	public static class Guild {

	}
}
