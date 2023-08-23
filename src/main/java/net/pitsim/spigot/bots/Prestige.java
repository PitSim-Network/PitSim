package net.pitsim.spigot.bots;

import org.bukkit.ChatColor;

import java.util.Random;

public enum Prestige {

	PRESTIGE_0(0, ChatColor.GRAY),
	PRESTIGE_1(4, ChatColor.BLUE),
	PRESTIGE_5(9, ChatColor.YELLOW),
	PRESTIGE_10(14, ChatColor.GOLD);

	final int max;
	final ChatColor color;

	Prestige(int max, ChatColor color) {
		this.max = max;
		this.color = color;
	}

	public static Prestige getRandom() {
		double rand = Math.random();
		if(rand > 0.2) return PRESTIGE_0;

		return values()[(int) (Math.random() * values().length)];
	}
}
