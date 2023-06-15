package net.pitsim.pitsim.enums;

import org.bukkit.ChatColor;

public enum UIColor {
	DARK_RED(ChatColor.DARK_RED, 14),
	RED(ChatColor.RED, 14),
	GOLD(ChatColor.GOLD, 1),
	YELLOW(ChatColor.YELLOW, 4),
	GREEN(ChatColor.GREEN, 5),
	DARK_GREEN(ChatColor.DARK_GREEN, 13),
	AQUA(ChatColor.AQUA, 3),
	DARK_AQUA(ChatColor.DARK_AQUA, 9),
	BLUE(ChatColor.BLUE, 11),
	DARK_BLUE(ChatColor.DARK_BLUE, 11),
	LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, 2),
	DARK_PURPLE(ChatColor.DARK_PURPLE, 10),
	BLACK(ChatColor.BLACK, 15),
	DARK_GRAY(ChatColor.DARK_GRAY, 7),
	GRAY(ChatColor.GRAY, 7),
	WHITE(ChatColor.WHITE, 0);

	public ChatColor chatColor;
	public short data;

	UIColor(ChatColor chatColor, int data) {
		this.chatColor = chatColor;
		this.data = (short) data;
	}
}
