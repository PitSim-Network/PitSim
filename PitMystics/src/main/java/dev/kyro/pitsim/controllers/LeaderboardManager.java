package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardManager {
	public static List<Leaderboard> leaderboards = new ArrayList<>();
	public static List<Map.Entry<UUID, APlayer>> queue = new ArrayList<>();

	public static void init() {

		new BukkitRunnable() {
			int amount = 0;
			@Override
			public void run() {
				File directory = new File("plugins/PitRemake/playerdata");
				File[] files = directory.listFiles();
				assert files != null;
				for(File file : files) {
					FileConfiguration data = YamlConfiguration.loadConfiguration(file);
					boolean shouldDelete = false;
					if(data.getInt("level") == 1 && data.getInt("prestige") == 0) shouldDelete = true;
					if(!data.contains("prestige")) shouldDelete = true;
					if(file.length() == 0) shouldDelete = true;
					if(shouldDelete) {
						file.delete();
						amount++;
						System.out.println("deleted: " + file.getName());
					}
				}
			}
		}.runTaskAsynchronously(PitSim.INSTANCE);

//		if(!AConfig.getString("server").equals("pitsim-main")) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, APlayer> entry : APlayerData.getAllData().entrySet()) {
					for(Leaderboard leaderboard : leaderboards) {
						leaderboard.calculate(entry.getKey(), entry.getValue());
					}
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
		new BukkitRunnable() {
			@Override
			public void run() {
				for(int i = 0; i < 20; i++) {
					if(queue.isEmpty()) {
						queue.addAll(APlayerData.getAllData().entrySet());
//						Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lDEBUG! &7Refilling leaderboard queue!"));
					}
					Map.Entry<UUID, APlayer> entry = queue.remove(0);
					for(Leaderboard leaderboard : leaderboards) leaderboard.calculate(entry.getKey(), entry.getValue());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 60 + 10, 20);
	}

	public static void registerLeaderboard(Leaderboard leaderboard) {
		leaderboards.add(leaderboard);
	}
}


