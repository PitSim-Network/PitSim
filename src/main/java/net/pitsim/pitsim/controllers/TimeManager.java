package net.pitsim.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.PitCalendarEvent;
import net.pitsim.pitsim.events.PitJoinEvent;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TimeManager implements Listener {
	private static final Map<PitCalendarEvent, Boolean> activeEventsMap = new HashMap<>();

	static {
		for(PitCalendarEvent calendarEvent : PitCalendarEvent.values())
			activeEventsMap.put(calendarEvent, calendarEvent.isCurrentlyActive());
	}

	public static boolean isEventActive(PitCalendarEvent calendarEvent) {
		return activeEventsMap.get(calendarEvent);
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		if(!isEventActive(PitCalendarEvent.HALLOWEEN)) return;
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.prestige < 5) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				Misc.sendTitle(player, "&5&lHAPPY &6&lHALLOWEEN!", 40);
				Misc.sendSubTitle(player, "&72x &fsouls&7 from the &5darkzone&7!", 40);
				AOutput.send(player, "&5&lHAPPY &6&lHALLOWEEN!&7 2x &5souls&7 from the darkzone!");
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

//	TODO: No longer does anything, needs to be re-added to code
	public static double getHalloweenSoulMultiplier() {
		return isEventActive(PitCalendarEvent.HALLOWEEN) ? 2 : 1;
	}
}
