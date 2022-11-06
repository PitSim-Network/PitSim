package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardManager {
	public static List<Leaderboard> leaderboards = new ArrayList<>();
	public static List<Map.Entry<UUID, APlayer>> queue = new ArrayList<>();

	public static void init() {

//		new BukkitRunnable() {
//			int amount = 0;
//			@Override
//			public void run() {
//				File directory = new File("plugins/PitRemake/playerdata");
//				File[] files = directory.listFiles();
//				assert files != null;
//				for(File file : files) {
//					FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//					boolean shouldDelete = false;
//					if(data.getInt("level") == 1 && data.getInt("prestige") == 0) shouldDelete = true;
//					if(!data.contains("prestige")) shouldDelete = true;
//					if(file.length() == 0) shouldDelete = true;
//					if(shouldDelete) {
//						file.delete();
//						amount++;
//						System.out.println("deleted: " + file.getName());
//					}
//				}
//			}
//		}.runTaskAsynchronously(PitSim.INSTANCE);

//		if(!AConfig.getString("server").equals("pitsim-main")) return;

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
		}.runTaskTimer(PitSim.INSTANCE, 20 * 60 + 10, 20);
	}

	public static void registerLeaderboard(Leaderboard leaderboard) {
		leaderboards.add(leaderboard);
	}

	public static Leaderboard getLeaderboard(String name) {
		for(Leaderboard leaderboard : leaderboards) {
			if(leaderboard.refName.equalsIgnoreCase(name)) return leaderboard;
		}
		return null;
	}
}


