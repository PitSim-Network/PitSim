package dev.kyro.pitsim.controllers;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelmetSystem {
	public static int increment = 200_000;
	public enum Passive {
		XP_BOOST("&bXP Boost", 3, 3, 10, (short) 12, ChatColor.AQUA, 1),
		GOLD_BOOST("&6Gold Boost", 4, 4, 25, (short) 14, ChatColor.GOLD, 1),
		DAMAGE("&cDamage Dealt",10, 5, 10, (short) 1, ChatColor.RED, 1),
		DAMAGE_REDUCTION("&9Damage Received",10, 10, 10, (short) 6, ChatColor.BLUE, 1),
		SHARD_CHANCE("&aShard Chance", 7, 35, 10, (short) 10, ChatColor.GREEN, 0.1);

		public String refName;
		public int everyX;
		public int level1Unlock;
		public int maxStacks;
		public short data;
		public ChatColor color;
		public double baseUnit;

		Passive(String refName, int everyX, int level1Unlock, int maxStacks, short data, ChatColor color, double baseUnit) {
			this.refName = refName;
			this.everyX = everyX;
			this.level1Unlock = level1Unlock;
			this.maxStacks = maxStacks;
			this.data = data;
			this.color = color;
			this.baseUnit = baseUnit;
		}

		public boolean isIncrementedAtLevel(int level) {

			level -= level1Unlock;
			if(level < 0 || level / everyX > maxStacks) return false;
			return level % everyX == 0 && level < everyX * maxStacks;
		}
	}

	public static List<Passive> getLevelData(int level) {
		List<Passive> incrementedPassives = new ArrayList<>();
		for(Passive passive : Passive.values()) if(passive.isIncrementedAtLevel(level)) incrementedPassives.add(passive);
		return incrementedPassives;
	}

	public static int getTotalStacks(Passive passive, int level) {
		if(level - passive.level1Unlock < 0) return 0;
		return Math.min(((level - passive.level1Unlock) / passive.everyX) + 1, passive.maxStacks);
	}

	/////////////////////////////////////////////////////////////////

	private static final Map<Integer, Integer> levelMap = new HashMap<>();
	static {
		int total = 0;
		for(int i = 0; i < 1000; i++) {
			total += increment * (i + 1);
			levelMap.put(i + 1, total);
		}
	}

	public static int getTotalGoldAtLevel(int level) {
		return levelMap.get(level);
	}

	public static int getLevel(int gold) {
		if(gold > levelMap.get(100)) return 101;
		for(Map.Entry<Integer, Integer> entry : levelMap.entrySet()) {
			if(entry.getValue() > gold) return entry.getKey();
		}
		return -1;
	}

	public static int getGoldToNextLevel(int gold) {
		if(gold < levelMap.get(1)) return increment - gold;
		if(gold > levelMap.get(100)) return 0;
		for(Map.Entry<Integer, Integer> entry : levelMap.entrySet()) {
			if(entry.getValue() > gold) return entry.getValue() - gold;
		}
		return -1;
	}
}