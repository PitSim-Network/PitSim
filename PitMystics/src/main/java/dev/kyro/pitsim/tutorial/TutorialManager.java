package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.tutorial.objects.Tutorial;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TutorialManager {
	public static Map<Player, Tutorial> tutorials = new HashMap<>();

	public static void createTutorial(Player player) {
		Tutorial tutorial = new Tutorial(player);
		tutorials.put(player, tutorial);
	}

	public static Tutorial getTutorial(Player player) {
		if(!tutorials.containsKey(player)) return null;
		return tutorials.get(player);
	}
}
