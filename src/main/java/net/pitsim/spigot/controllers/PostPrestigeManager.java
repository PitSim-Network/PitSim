package net.pitsim.spigot.controllers;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostPrestigeManager {
	public static final String STAR_CHAR = "\u272c";

	public static Map<Double, ChatColor> colorMap = new LinkedHashMap<>();

	static {
		colorMap.put(1D, ChatColor.GRAY);
		colorMap.put(1_000_000_000D, ChatColor.BLUE);
		colorMap.put(2_500_000_000D, ChatColor.YELLOW);
		colorMap.put(5_000_000_000D, ChatColor.GOLD);
		colorMap.put(10_000_000_000D, ChatColor.RED);
		colorMap.put(20_000_000_000D, ChatColor.DARK_PURPLE);
		colorMap.put(35_000_000_000D, ChatColor.LIGHT_PURPLE);
		colorMap.put(50_000_000_000D, ChatColor.WHITE);
		colorMap.put(75_000_000_000D, ChatColor.AQUA);
		colorMap.put(100_000_000_000D, ChatColor.DARK_AQUA);
		colorMap.put(125_000_000_000D, ChatColor.DARK_BLUE);
		colorMap.put(150_000_000_000D, ChatColor.GREEN);
		colorMap.put(200_000_000_000D, ChatColor.BLACK);
		colorMap.put(Double.MAX_VALUE, ChatColor.DARK_RED);


	}

	public static String getStarString(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return getStarString(pitPlayer.overflowXP);
	}

	public static String getStarString(double xp) {
		if(xp < 1) return "";

		ChatColor color = null;
		for(Map.Entry<Double, ChatColor> entry : colorMap.entrySet()) {
			if(xp < entry.getKey()) break;

			color = entry.getValue();
		}

		if(color == null) return "";

		return color + STAR_CHAR;
	}

	public static String getStarString(int index) {
		List<ChatColor> colors = new ArrayList<>(colorMap.values());
		return colors.get(index) + STAR_CHAR;
	}

	public static int getStarIndex(double xp) {
		int i = -1;

		for(Map.Entry<Double, ChatColor> entry : colorMap.entrySet()) {
			if(xp < entry.getKey()) break;
			i++;
		}

		return i;
	}

	public static double getXP(int index) {
		List<Double> xp = new ArrayList<>(colorMap.keySet());
		return xp.get(index);
	}



	public static String getProgressionString(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		int index = getStarIndex(pitPlayer.overflowXP);
		String current = getStarString(index);
		String next = getStarString(index + 1);

		double percent = (pitPlayer.overflowXP - getXP(index)) / (getXP(index + 1) - getXP(index));

		DecimalFormat formatter = new DecimalFormat("##.##");

		return current + " &8\u27A1 " + AUtil.createProgressBar("|", ChatColor.GREEN, ChatColor.GRAY, 50, percent)
				+ " &8\u27A1 " + next + " &8(" + formatter.format(percent * 100) + "%)";
	}

	public static double getNextUnlockDisplayXP(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		int index = getStarIndex(pitPlayer.overflowXP);
		return getXP(index + 1) + PrestigeValues.getTotalXP(pitPlayer.prestige, pitPlayer.level, 0);
	}
}
