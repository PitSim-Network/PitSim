package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Booster;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BoosterManager implements Listener {
	public static List<Booster> boosterList = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Booster booster : boosterList) {
					if(booster.minutes == 0) continue;
					booster.minutes--;
					if(booster.minutes == 0) booster.disable(); else booster.updateTime();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20 * 60);
	}

	public static void registerBooster(Booster booster) {
		boosterList.add(booster);
		Bukkit.getServer().getPluginManager().registerEvents(booster, PitSim.INSTANCE);
	}

	public static Booster getBooster(String refName) {
		for(Booster booster : boosterList) if(booster.refName.equalsIgnoreCase(refName)) return booster;
		return null;
	}

	public static void addTime(Booster booster, int minutes) {
		if(minutes == 0) return;
		booster.minutes += minutes;
		booster.updateTime();
	}
}
