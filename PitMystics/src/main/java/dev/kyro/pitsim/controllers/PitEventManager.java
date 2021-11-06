package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
//import net.kyori.adventure.audience.Audience;
//import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class PitEventManager {
	public static List<PitEvent> events = new ArrayList<>();
	public static Map<Player, Double> kills = new HashMap<>();
	public static Map<Player, Integer> bounty = new HashMap<>();
	public static Boolean majorEvent = false;
	public static PitEvent activeEvent;
	public static Boolean preparingEvent = false;
	public static Boolean canceledEvent = false;

	public static void registerPitEvent(PitEvent event) {
		events.add(event);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(event, PitSim.INSTANCE);
	}


	public static void eventWait() {

		new BukkitRunnable() {
			@Override
			public void run() {
				if(AFKManager.onlineActivePlayers < 4) return;

				PitEvent randomEvent = getRandomEvent(events);
				startTimer(randomEvent);

				File file = new File("plugins/NoteBlockAPI/Effects/major.nbs");
				Song song = NBSDecoder.parse(file);
				RadioSongPlayer rsp = new RadioSongPlayer(song);
				rsp.setRepeatMode(RepeatMode.NO);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMAJOR EVENT! " +
						" " + randomEvent.color + ""  + ChatColor.BOLD + randomEvent.name.toUpperCase(Locale.ROOT) + " &7in 3 minutes [&e&lINFO&7]"));

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {

					rsp.addPlayer(onlinePlayer);
				}
				rsp.setPlaying(true);
				preparingEvent = true;

				new BukkitRunnable() {
					@Override
					public void run() {
						timerBar(ChatColor.translateAlternateColorCodes('&',
								"&5&lMAJOR EVENT! " + randomEvent.color + "" + ChatColor.BOLD +
										randomEvent.getName().toUpperCase(Locale.ROOT)) + "! &7Starting in", 3, 0, ChatColor.YELLOW);
					}
				}.runTaskLater(PitSim.INSTANCE, 10L);
				randomEvent.prepare();
			}
		}.runTaskTimer(PitSim.INSTANCE, 600, 45600);
	}

	public static void startTimer(PitEvent event) {

		new BukkitRunnable() {
			@Override
			public void run() {
				if(AFKManager.onlineActivePlayers < 4) {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lEVENT CANCELED! &7Not enough active players!"));
					canceledEvent = true;
					return;
				}

				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5&lMAJOR EVENT! " +
						" " + event.color + ""  + ChatColor.BOLD + event.name.toUpperCase(Locale.ROOT) + " &7starting now"));
				endTimer(event);
				try {
					event.start();
				} catch(Exception e) {
					e.printStackTrace();
				}
				majorEvent = true;
				for(Player player : Bukkit.getOnlinePlayers()) {
					preparingEvent = false;
					Misc.sendTitle(player,event.color + "" + ChatColor.BOLD + "PIT EVENT!", 50);
					Misc.sendSubTitle(player, event.color + "" + ChatColor.BOLD + event.name.toUpperCase(Locale.ROOT), 50);
					Sounds.EVENT_START.play(player);
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					kills.put(player, pitPlayer.getKills());
					bounty.put(player, pitPlayer.bounty);
					pitPlayer.setKills(0);
					pitPlayer.bounty = 0;
				}

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					FeatherBoardAPI.showScoreboard(onlinePlayer, "event");
				}

				for(Non non : NonManager.nons) {
					non.setDisabled(true);
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						timerBar(ChatColor.translateAlternateColorCodes('&',
								"&5&lMAJOR EVENT! " + event.color + "" + ChatColor.BOLD +
										event.getName().toUpperCase(Locale.ROOT)) + "! &7Ending in", 5, 0, ChatColor.GREEN);
					}
				}.runTaskLater(PitSim.INSTANCE, 10L);
			}
		}.runTaskLater(PitSim.INSTANCE, 3600);
	}

	public static void endTimer(PitEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {
               if(majorEvent && activeEvent != null) endEvent(event);
               canceledEvent = false;
            }
        }.runTaskLater(PitSim.INSTANCE, 6000L);
    }

	public static void endEvent(PitEvent event) {
		SpawnManager.postMajor = true;
		new BukkitRunnable() {
			@Override
			public void run() {
				SpawnManager.postMajor = false;
			}
		}.runTaskLater(PitSim.INSTANCE, 30 * 20L);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//			BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//			Audience audience = PitSim.INSTANCE.adventure().player(onlinePlayer);
//			manager.hideActiveBossBar(audience);
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
			if(kills.containsKey(onlinePlayer)) pitPlayer.setKills(kills.get(onlinePlayer));
			if(bounty.containsKey(onlinePlayer)) pitPlayer.bounty = 0;
			kills.remove(onlinePlayer);
			bounty.remove(onlinePlayer);
		}
		event.end();
		for(Non non : NonManager.nons) {
			non.setDisabled(false);
		}

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			FeatherBoardAPI.removeScoreboardOverride(onlinePlayer, "event");
			FeatherBoardAPI.resetDefaultScoreboard(onlinePlayer);

//			BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//			Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//			manager.hideActiveBossBar(audiences);
		}
		majorEvent = false;
	}

	public static PitEvent getRandomEvent(List<PitEvent> events) {
		Random rand = new Random();
		return events.get(rand.nextInt(events.size()));
	}

	public static void timerBar(String message, int startminutes, int startseconds, ChatColor numcolor) {

		new BukkitRunnable() {
			int minutes = startminutes;
			int seconds = startseconds;

			@Override
			public void run() {
				if(!majorEvent && !preparingEvent) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//						BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//						Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//						manager.hideActiveBossBar(audiences);
					}
					this.cancel();
				}
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//					BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//					Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//					manager.showMyBossBar(audiences);
				}
				if(seconds > 0) {
					seconds = seconds - 1;
				} else {
					if(minutes > 0) {
						minutes = minutes - 1;
						seconds = 59;
					} else {
						for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//							BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//							Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//							manager.hideActiveBossBar(audiences);
						}
						this.cancel();
					}

				}

				float first = (float) (minutes * 60) + (float) seconds;
				float second = (float) (startminutes * 60) + (float) startseconds;
				float decimal = first / second;
				String finalSeconds = (seconds < 10 ? "0" : "") + seconds;
				String finalMinutes = (minutes < 10 ? "0" : "") + minutes;
//				Component newComponent = Component.text(message + " " + numcolor + finalMinutes + ":" + finalSeconds);
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//					BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//					Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//					manager.defaultBar.name(newComponent);
//					manager.defaultBar.progress(decimal);
				}
//				Bukkit.broadcastMessage(String.valueOf((minutes * 60) + seconds));
				if(!majorEvent && !preparingEvent) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//						BossBarManager manager = PlayerManager.bossBars.get(onlinePlayer);
//						Audience audiences = PitSim.INSTANCE.adventure().player(onlinePlayer);
//						manager.hideActiveBossBar(audiences);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

	}
}
