package dev.kyro.pitsim.controllers;

import dev.kyro.arcticguilds.controllers.GuildManager;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
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
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60L);
	}
}
