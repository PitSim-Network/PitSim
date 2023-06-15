package net.pitsim.spigot.controllers;

import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.PitJoinEvent;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.storage.StorageManager;
import net.pitsim.spigot.storage.StorageProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomEventManager implements Listener {

	public static List<UUID> preventedJoin = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInitialJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		StorageProfile profile = StorageManager.getProfile(player.getUniqueId());

		if(!profile.isLoaded()) {
			preventedJoin.add(player.getUniqueId());
			player.kickPlayer(ChatColor.RED + "An error occurred when loading your data. Please report this issue.");
			return;
		}

		PitJoinEvent pitJoinEvent = new PitJoinEvent(event, player, pitPlayer, profile);

		try {
			Bukkit.getPluginManager().callEvent(pitJoinEvent);
		} catch(Exception e) {
			e.printStackTrace();
			preventedJoin.add(player.getUniqueId());
			player.kickPlayer(ChatColor.RED + "An error occurred when loading your data. Please report this issue.");
		}
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		StorageProfile profile = StorageManager.getProfile(player.getUniqueId());

		if(!preventedJoin.contains(player.getUniqueId())) {
			PitQuitEvent pitQuitEvent = new PitQuitEvent(event, player, pitPlayer, profile);
			Bukkit.getPluginManager().callEvent(pitQuitEvent);
		} else {
			PlayerDataManager.removePitPlayer(pitPlayer);
		}

		preventedJoin.remove(player.getUniqueId());
	}


}
