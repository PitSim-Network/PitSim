package net.pitsim.spigot.bots;

import org.bukkit.ChatColor;

public enum PitLevel {

	LEVEL_1(1, ChatColor.GRAY, false),
	LEVEL_10(10, ChatColor.BLUE, false),
	LEVEL_20(20, ChatColor.DARK_AQUA, false),
	LEVEL_30(30, ChatColor.DARK_GREEN, false),
	LEVEL_40(40, ChatColor.GREEN, false),
	LEVEL_50(50, ChatColor.YELLOW, false),
	LEVEL_60(60, ChatColor.GOLD, true),
	LEVEL_70(70, ChatColor.RED, true),
	LEVEL_80(80, ChatColor.DARK_RED, true),
	LEVEL_90(90, ChatColor.DARK_PURPLE, true),
	LEVEL_100(100, ChatColor.LIGHT_PURPLE, true),
	LEVEL_110(110, ChatColor.WHITE, true),
	LEVEL_120(120, ChatColor.AQUA, true);


	final int min;
	final ChatColor color;
	final boolean bold;

	PitLevel(int min, ChatColor color, boolean bold) {
		this.min = min;
		this.color = color;
		this.bold = bold;
	}

	public static PitLevel getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
