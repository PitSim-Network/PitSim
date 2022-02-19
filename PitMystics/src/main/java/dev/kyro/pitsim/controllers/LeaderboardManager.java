package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardManager {

	public static Map<FileConfiguration, Integer> levels = new HashMap<>();
	public static Map<FileConfiguration, Integer> sortedMap = new HashMap<>();

	public static boolean hasRan = false;
	public static int amount = 0;

	public static void init() {
		new BukkitRunnable() {
			@Override
			public void run() {
				calculate();
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, 0L, 4000L);
	}

	public static void calculate() {
		sortedMap.clear();
		levels.clear();

		File directory = new File("plugins/PitRemake/playerdata");
		File[] files = directory.listFiles();
		assert files != null;
		for(File file : files) {
			FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//			if(!hasRan) {
//				boolean shouldDelete = false;
//				if(data.getInt("level") == 1 && data.getInt("prestige") == 0) shouldDelete = true;
//				if(file.length() == 0) shouldDelete = true;
//				if(shouldDelete) {
//                	file.delete();
//					amount++;
//					System.out.println("deleted: " + file.getName());
//				}
//			}
			levels.put(data, (1000 * data.getInt("prestige") + data.getInt("level")));
		}
//		if(!hasRan) System.out.println(amount);
//		hasRan = true;

		sortedMap = levels.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getValue))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(a, b) -> {
							throw new AssertionError();
						},
						LinkedHashMap::new
				));
	}
}


