package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.Non;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager implements Listener {
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
				for(World world : MapManager.currentMap.lobbies) {
					if(!MapManager.multiLobbies && world != MapManager.currentMap.firstLobby) continue;
					for(int i = 0; i < 3; i++) {
						if(getNonsInLobby(world) >= getMaxNons(world)) break;

						Non non = new Non(skinLoadedBotIGNS.get((int) (Math.random() * skinLoadedBotIGNS.size())), world);
						new BukkitRunnable() {
							@Override
							public void run() {
								non.remove();
							}
						}.runTaskLater(PitSim.INSTANCE, (long) (20 * 60 * (Math.random() * 10 + 5)));
					}
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

	public static int getNonsInLobby(World world) {
		int nonCount = 0;
		for(Non non : nons) if(non.world == world) nonCount++;
		return nonCount;
	}

	public static int getMaxNons(World world) {
		int base = 25;
		int max = 40;

		Booster booster = BoosterManager.getBooster("chaos");
		if(booster.isActive()) {
			max = 60;
			base = 60;
		}

		Location mid = MapManager.currentMap.getMid(world);
		int playersNearMid = 0;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != world) continue;
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
		for(String name : skinLoadedBotIGNS) {
			if(newBotIGNs.contains(name)) continue;
			skinLoadedBotIGNS.remove(name);
		}
		for(String name : botIGNs) {
			if(newBotIGNs.contains(name)) continue;
			botIGNs.remove(name);
		}
	}

	public static Non getNon(LivingEntity entity) {
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
