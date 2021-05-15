package dev.kyro.pitsim.enums;

import org.bukkit.ChatColor;

public enum PantColor {

	RED("red", ChatColor.RED, 0xFF5555),
	ORANGE("orange", ChatColor.GOLD, 0xFFAA00),
	YELLOW("yellow", ChatColor.YELLOW, 0xFFFF55),
	GREEN("green", ChatColor.GREEN, 0x55FF55),
	BLUE("blue", ChatColor.BLUE, 0x5555FF);
//	DARK("", ChatColor.DARK_PURPLE, 0x000000),
//	SEWER("", ChatColor.DARK_AQUA, 0x7DC383),
//	AQUA("", ChatColor.DARK_AQUA, 0x55FFFF);

	public String refName;
	public ChatColor chatColor;
	public int hexColor;

	PantColor(String refName, ChatColor chatColor, int hexColor) {
		this.refName = refName;
		this.chatColor = chatColor;
		this.hexColor = hexColor;
	}

	public static PantColor getPantColor(String refName) {

		for(PantColor pantColor : values()) {

			if(pantColor.refName.equalsIgnoreCase(refName)) return pantColor;
		}
		return null;
	}
}
