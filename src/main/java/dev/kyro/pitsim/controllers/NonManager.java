package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.boosters.ChaosBooster;
import dev.kyro.pitsim.controllers.objects.Non;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager {
	public static List<String> botIGNs = new ArrayList<>();
	public static boolean defaultNons = true;
	public static List<String> skinLoadedBotIGNS = new ArrayList<>();
	public static List<Non> nons = new ArrayList<>();

	static {
		botIGNs.add("KyroKrypt");
		botIGNs.add("BHunter");
		botIGNs.add("PayForTruce");
		botIGNs.add("Fishduper");
		botIGNs.add("wiji1");
		botIGNs.add("Muruseni");
		botIGNs.add("ObvEndyy");
	}

	public static final int MAX_DISTANCE_FROM_MID = 10;

	public static void init() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!FirestoreManager.CONFIG.nons) return;

				for(String botIGN : new ArrayList<>(botIGNs)) {
					if(!SkinManager.isSkinLoaded(botIGN)) {
						SkinManager.loadSkin(botIGN);
						continue;
					}
					skinLoadedBotIGNS.add(botIGN);
					botIGNs.remove(botIGN);
				}

				if(skinLoadedBotIGNS.isEmpty()) return;
				for(int i = 0; i < 3; i++) {
					if(nons.size() >= getMaxNons()) break;

					Non non = new Non(skinLoadedBotIGNS.get((int) (Math.random() * skinLoadedBotIGNS.size())));
					new BukkitRunnable() {
						@Override
						public void run() {
							non.remove();
						}
					}.runTaskLater(PitSim.INSTANCE, (long) (20 * 60 * (Math.random() * 10 + 5)));
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 40L, 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : nons) non.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static int getMaxNons() {
		int base = 25;
		int max = 40;

		if(ChaosBooster.INSTANCE.isActive()) {
			max = 50;
			base = 50;
		}

		Location mid = MapManager.currentMap.getMid();
		int playersNearMid = 0;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != MapManager.currentMap.world) continue;
			double distance = mid.distance(onlinePlayer.getLocation());
			if(distance < 15) playersNearMid++;
		}
		return Math.min(playersNearMid * 3 + base, max);
	}

	public static void updateNons(List<String> newBotIGNs) {
		if(!newBotIGNs.contains("KyroKrypt")) newBotIGNs.add("KyroKrypt");
		if(defaultNons) {
			defaultNons = false;
			botIGNs.clear();
			skinLoadedBotIGNS.clear();
		}
		for(String name : newBotIGNs) {
			if(skinLoadedBotIGNS.contains(name) || botIGNs.contains(name)) continue;
			botIGNs.add(name);
		}

		List<String> skinLoadedRemove = new ArrayList<>();
		for(String name : skinLoadedBotIGNS) {
			if(newBotIGNs.contains(name)) continue;
			skinLoadedRemove.add(name);
		}
		skinLoadedRemove.forEach(skin -> skinLoadedBotIGNS.remove(skin));

		List<String> botIGNsRemove = new ArrayList<>();
		for(String name : botIGNs) {
			if(newBotIGNs.contains(name)) continue;
			botIGNsRemove.add(name);
		}
		botIGNsRemove.forEach(skin -> botIGNs.remove(skin));
	}

	public static Non getNon(LivingEntity entity) {
		if(entity == null) return null;
		try {
			for(Non non : nons) {
				if(non == null) continue;
				if(non.non.getUniqueId().equals(entity.getUniqueId())) return non;
			}
		} catch(Exception exception) {
			return null;
		}
		return null;
	}
}
