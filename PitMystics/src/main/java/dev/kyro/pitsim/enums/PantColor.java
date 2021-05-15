package dev.kyro.pitsim.enums;

import org.bukkit.ChatColor;

public enum PantColor {

	RED("Red", ChatColor.RED, 0xFF5555),
	ORANGE("Orange", ChatColor.GOLD, 0xFFAA00),
	YELLOW("Yellow", ChatColor.YELLOW, 0xFFFF55),
	GREEN("Green", ChatColor.GREEN, 0x55FF55),
	BLUE("Blue", ChatColor.BLUE, 0x5555FF);
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
