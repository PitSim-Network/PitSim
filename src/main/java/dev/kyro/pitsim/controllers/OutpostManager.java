package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class OutpostManager implements Listener {
	public Guild activeGuild;
	public Guild contestingGuild;
	public double percentControlled;

	public OutpostManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Guild contestingGuild = getContestingGuild();
				if(contestingGuild == null && ) {

				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public static void increaseControl() {

	}

	public static Guild getContestingGuild() {
//		TODO: implement
//		should return null if the activeGuild == the guild in the outpost, otherwise return the guild in the outpost
//		if there are multiple guilds in the outpost, it should return null
	}

//	TODO: Implement this in ArcticGuilds
	public static class Guild {

	}
}
