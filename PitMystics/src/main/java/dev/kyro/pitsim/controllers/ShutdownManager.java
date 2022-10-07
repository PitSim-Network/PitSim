package dev.kyro.pitsim.controllers;

import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShutdownManager {

	public static int minutes = 0;
	public static int seconds = 0;
	public static int counter = 0;
	public static boolean enderchestDisabled = false;
	public static boolean isShuttingDown = false;


	public static void initiateShutdown(int minute) {
		isShuttingDown = true;
		minutes = minute;

		new BukkitRunnable() {
			@Override
			public void run() {
				counter++;
				if(counter >= 20) {
					counter = 0;
					if(seconds > 0) {
						if(seconds == 2 && minutes == 0) {
							ProxyMessaging.sendShutdown();
						}
						seconds--;
					} else {
						if(minutes > 0) {
							minutes--;
							seconds = 60;
						} else {
							execute();
						}
					}
				}

				if(seconds == 0 && counter == 1) {
					if(minutes == 1) {
						disableEnderChest();
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN IN "
								+ minutes + " MINUTE!");
					} else if(minutes != 0) {
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN IN "
								+ minutes + " MINUTES!");
					} else {
						Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "INSTANCE SHUTDOWN!");
					}
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						Sounds.CTF_FLAG_STOLEN.play(onlinePlayer);
						onlinePlayer.closeInventory();
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 1, 1);
	}

	public static void execute() {
		PitSim.client.retrieveServerByIdentifier(AConfig.getString("pterodactyl-id"))
				.flatMap(ClientServer::restart).executeAsync();
	}

	public static void disableEnderChest() {
		enderchestDisabled = true;
	}
}
