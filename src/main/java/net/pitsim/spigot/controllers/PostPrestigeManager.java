package net.pitsim.spigot.controllers;

import be.maximvdw.featherboard.P;
import be.maximvdw.featherboard.S;
import io.opencensus.trace.Link;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostPrestigeManager {
	public static final String STAR_CHAR = "\u272c";

	public static Map<Double, ChatColor> colorMap = new LinkedHashMap<>();

	static {
		colorMap.put(1D, ChatColor.GRAY);
		colorMap.put(1000000000D, ChatColor.BLUE);
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
}
