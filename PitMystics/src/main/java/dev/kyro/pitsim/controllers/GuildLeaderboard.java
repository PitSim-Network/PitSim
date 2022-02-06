package dev.kyro.pitsim.controllers;

import dev.kyro.arcticguilds.controllers.GuildManager;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.pitsim.PitSim;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GuildLeaderboard {

	public static List<Guild> topGuilds;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				topGuilds = GuildManager.getTopGuilds();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 5L, 20 * 60L);
	}
}
