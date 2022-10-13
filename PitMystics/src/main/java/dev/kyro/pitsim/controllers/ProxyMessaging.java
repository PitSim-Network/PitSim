package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.controllers.objects.ServerData;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.inventories.KeeperPanel;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProxyMessaging implements Listener {

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);
	}


	public static void sendStartup() {
		System.out.println(PitSim.serverName);
		new PluginMessage().writeString("INITIATE STARTUP").writeString(PitSim.serverName).send();
	}

	public static void sendShutdown() {
		new PluginMessage().writeString("INITIATE FINAL SHUTDOWN").writeString(PitSim.serverName).send();


		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			LobbySwitchManager.setSwitchingPlayer(player);

			if(PitSim.isDarkzone()) {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						new PluginMessage().writeString("QUEUE DARKZONE").writeString(player.getName()).send();
					}
				};

				try {
					pitPlayer.save(true, runnable);
				} catch(ExecutionException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			} else {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						new PluginMessage().writeString("QUEUE").writeString(player.getName()).writeBoolean(true).send();
					}
				};

				try {
					pitPlayer.save(true, runnable);
				} catch(ExecutionException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static void sendServerData() {
		PluginMessage message = new PluginMessage();
		message.writeString("SERVER DATA").writeString(PitSim.serverName);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(VanishAPI.isInvisible(onlinePlayer)) continue;

			String builder = PrestigeValues.getPlayerPrefix(onlinePlayer) +
					PlaceholderAPI.setPlaceholders(onlinePlayer, " %luckperms_prefix%%player_name%");
			message.writeString(builder);
		}
		message.send();
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = event.getMessage().getStrings();
		List<Integer> integers = event.getMessage().getIntegers();
		List<Boolean> booleans = event.getMessage().getBooleans();

		if(strings.size() >= 1 && strings.get(0).equals("SERVER DATA")) {
			strings.remove(0);

			for(int i = 0; i < integers.size(); i++) {

				new ServerData(i, strings, integers, booleans);
			}
		}

		if(strings.size() >= 1 && booleans.size() >= 1 && strings.get(0).equals("SHUTDOWN")) {

			int minutes = 5;
			if(integers.size() >= 1 && integers.get(0) > 0) {
				minutes = integers.get(0);
			}

			ShutdownManager.isRestart = booleans.get(0);
			ShutdownManager.initiateShutdown(minutes);
		}

		if(strings.size() >= 2 && strings.get(0).equals("SAVE DATA")) {

			String playerName = strings.get(1);
			Player player = Bukkit.getPlayer(playerName);
			if(player == null) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			new BukkitRunnable() {
				@Override
				public void run() {
					LobbySwitchManager.setSwitchingPlayer(player);
				}
			}.runTask(PitSim.INSTANCE);

			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					new PluginMessage().writeString("QUEUE").writeString(player.getName()).writeInt(0).send();
				}
			};

			try {
				pitPlayer.save(true, runnable);
			} catch(ExecutionException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if(strings.size() >= 2 && strings.get(0).equals("DARKZONE JOIN")) {
			System.out.println(1);
			if(booleans.size() >= 1 && booleans.get(0)) {
				System.out.println(2);
				UUID uuid = UUID.fromString(strings.get(1));
				LobbySwitchManager.joinedFromDarkzone.add(uuid);
				System.out.println(uuid + "joined from darkzone");

				new BukkitRunnable() {
					@Override
					public void run() {
						LobbySwitchManager.joinedFromDarkzone.remove(uuid);
					}
				}.runTaskLater(PitSim.INSTANCE, 20 * 5);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

}
