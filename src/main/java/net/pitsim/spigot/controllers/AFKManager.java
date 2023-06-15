package net.pitsim.spigot.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AFKManager implements Listener {

	public static List<Player> AFKPlayers = new ArrayList<>();
	public static Map<Player, Location> lastLocation = new HashMap<>();
	public static Map<Player, Integer> AFKRotations = new HashMap<>();
	public static Integer onlineActivePlayers = 0;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				onlineActivePlayers = 0;
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(AFKPlayers.contains(player)) continue;

					onlineActivePlayers++;
					Location playerLoc = player.getLocation();
					if(lastLocation.containsKey(player)) {
						playerLoc.setY(0);
						if(playerLoc.equals(lastLocation.get(player))) {
							if(AFKRotations.containsKey(player)) AFKRotations.put(player, AFKRotations.get(player) + 1);
							else AFKRotations.put(player, 1);
						}
					}
					lastLocation.put(player, playerLoc);

					if(AFKRotations.containsKey(player) && AFKRotations.get(player) >= 6) {
						AFKPlayers.add(player);
						AOutput.send(player, "&cYou are now AFK!");
						AFKRotations.remove(player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 300L);

	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		boolean moved = false;
		if(!AFKPlayers.contains(event.getPlayer())) return;

		if(event.getPlayer().getLocation().getBlockX() != lastLocation.get(event.getPlayer()).getBlockX()) moved = true;
//		if(event.getPlayer().getLocation().getBlockY() != lastLocation.get(event.getPlayer()).getBlockY()) moved = true;
		if(event.getPlayer().getLocation().getBlockZ() != lastLocation.get(event.getPlayer()).getBlockZ()) moved = true;

		if(moved) {
			AFKPlayers.remove(event.getPlayer());
			AOutput.send(event.getPlayer(), "&cYou are no longer AFK!");
			onlineActivePlayers = 0;
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!AFKPlayers.contains(onlinePlayer)) onlineActivePlayers++;
			}
		}
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		AFKPlayers.remove(event.getPlayer());

//		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c-&8] &6" + event.getPlayer().getDisplayName() + " &ehas left"));
	}
}
