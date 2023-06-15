package net.pitsim.pitsim.controllers;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.Leaderboard;
import net.pitsim.pitsim.controllers.objects.LeaderboardData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LeaderboardManager {
	public static List<Leaderboard> leaderboards = new ArrayList<>();

	public static void init() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Leaderboard leaderboard : leaderboards) {
					LeaderboardData data = LeaderboardData.getLeaderboardData(leaderboard);
					if(data == null) continue;

					for(UUID uuid : data.getLeaderboardDataMap().keySet()) {
						leaderboard.calculate(uuid);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20 * 60 + 10);
	}

	public static Leaderboard getLeaderboard(String refName) {
		for(Leaderboard leaderboard : leaderboards) {
			if(Objects.equals(leaderboard.refName, refName)) return leaderboard;
		}
		return null;
	}

	public static void registerLeaderboard(Leaderboard leaderboard) {
		leaderboards.add(leaderboard);
	}
}

