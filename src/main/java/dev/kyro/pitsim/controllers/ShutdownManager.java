package dev.kyro.pitsim.controllers;

import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ShutdownManager {

	public static int minutes = 0;
	public static int seconds = 0;
	public static int counter = 0;
	public static boolean enderchestDisabled = false;
	public static boolean isShuttingDown = false;

	public static boolean isRestart = false;

	public static BukkitTask runnable;

	public static void initiateShutdown(int minutes) {
		if(isShuttingDown) return;

		isShuttingDown = true;
		ShutdownManager.minutes = minutes;
		if(minutes == 0) seconds = 5;

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				counter++;
				if(counter >= 20) {
					counter = 0;
					if(seconds > 0) {
						if(seconds == 2 && ShutdownManager.minutes == 0) {
							ProxyMessaging.sendShutdown();
						}
						seconds--;
					} else {
						if(ShutdownManager.minutes > 0) {
							ShutdownManager.minutes--;
							seconds = 60;
						} else {
							execute();
						}
					}
				}

				if(seconds == 0 && counter == 1) {
					if(ShutdownManager.minutes == 1) {
						disableEnderChest();
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN IN "
								+ ShutdownManager.minutes + " MINUTE!");
					} else if(ShutdownManager.minutes != 0) {
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN IN "
								+ ShutdownManager.minutes + " MINUTES!");
					} else {
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN!");
					}
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						Sounds.CTF_FLAG_STOLEN.play(onlinePlayer);
						if(minutes == 0) onlinePlayer.closeInventory();
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 1, 1);
	}

	public static void execute() {
		if(isRestart) {
			PitSim.client.retrieveServerByIdentifier(AConfig.getString("pterodactyl-id"))
					.flatMap(ClientServer::restart).executeAsync();
		} else {
			PitSim.client.retrieveServerByIdentifier(AConfig.getString("pterodactyl-id"))
					.flatMap(ClientServer::stop).executeAsync();
		}

	}

	public static void cancelShutdown() {
		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN CANCELED!");
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Sounds.CTF_FLAG_STOLEN.play(onlinePlayer);
		}

		if(!isShuttingDown) return;
		isShuttingDown = false;
		runnable.cancel();
		seconds = 0;
		minutes = 0;
		counter = 0;
		enderchestDisabled = false;
		isRestart = false;
	}

	public static void disableEnderChest() {
		enderchestDisabled = true;
	}
}
