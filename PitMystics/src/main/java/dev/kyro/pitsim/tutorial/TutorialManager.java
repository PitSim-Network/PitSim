package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.tutorial.objects.Tutorial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class TutorialManager implements Listener {
	public static Map<Player, Tutorial> tutorials = new HashMap<>();

	public static void createTutorial(Player player) {
		Tutorial tutorial = new Tutorial(player, getOpenPosition());
		tutorials.put(player, tutorial);
	}


	public static Tutorial getTutorial(Player player) {
		if(!tutorials.containsKey(player)) return null;
		return tutorials.get(player);
	}

	public static int getOpenPosition() {
		int highestPosition = -1;
		for(Tutorial tutorial : tutorials.values()) {
			if(tutorial.position > highestPosition) highestPosition = tutorial.position;
		}
		return highestPosition + 1;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		TutorialManager.createTutorial(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(tutorials.containsKey(player)) tutorials.get(player).cleanUp();
	}
}
