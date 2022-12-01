package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeManager implements Listener {
	public static boolean isHalloween() {
		Calendar eventStart = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		Calendar eventEnd = Calendar.getInstance(TimeZone.getTimeZone("EST"));

		setDate(eventStart, Calendar.OCTOBER, 30);
		setDate(eventEnd, Calendar.NOVEMBER, 1);

		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		return currentTime.after(eventStart) && currentTime.before(eventEnd);
	}

	public static boolean isChristmasSeason() {
		Calendar eventStart = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		Calendar eventEnd = Calendar.getInstance(TimeZone.getTimeZone("EST"));

		setDate(eventStart, Calendar.NOVEMBER, 29);
		setDate(eventEnd, Calendar.JANUARY, 1);

		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		return currentTime.after(eventStart) && currentTime.before(eventEnd);
	}

	public static boolean isChristmasImminent() {
		Calendar eventStart = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		Calendar eventEnd = Calendar.getInstance(TimeZone.getTimeZone("EST"));

		setDate(eventStart, Calendar.DECEMBER, 24);
		setDate(eventEnd, Calendar.JANUARY, 28);

		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		return currentTime.after(eventStart) && currentTime.before(eventEnd);
	}

	private static void setDate(Calendar calendar, int month, int date) {
		calendar.clear();
		calendar.set(Calendar.YEAR,
				Calendar.getInstance(TimeZone.getTimeZone("EST")).get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(!isHalloween()) return;
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

	public static double getHalloweenSoulMultiplier() {
		return isHalloween() ? 2 : 1;
	}
}
