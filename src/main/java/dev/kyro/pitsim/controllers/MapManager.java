package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitMap;
import dev.kyro.pitsim.events.PlayerSpawnCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements Listener {
	public static List<PitMap> mapList = new ArrayList<>();
	public static PitMap currentMap;

	public static Location darkzoneSpawn = new Location(getDarkzone(), 178.5, 91, -93.5, -90, 0);

//	public static Location initialDarkzoneSpawn = new Location(getDarkzone(), 177.5, 92, -93.5, -90, 0);
	public static Location initialDarkzoneSpawn = new Location(getDarkzone(), 310, 69, -136, -90, 0);

	public static void onStart() {
		if(PitSim.status == PitSim.ServerStatus.DARKZONE) return;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(PitSim.status == PitSim.ServerStatus.OVERWORLD) onlinePlayer.teleport(currentMap.getSpawn());
			else onlinePlayer.teleport(darkzoneSpawn);
		}
	}

	public static void registerMap(PitMap pitMap) {
		mapList.add(pitMap);
		currentMap = pitMap;
	}

	public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);

	@EventHandler
	public void onSpawn(PlayerSpawnCommandEvent event) {
		Player player = event.getPlayer();
		if(player.getWorld() != MapManager.getDarkzone()) return;
		event.setCancelled(true);
		AOutput.error(event.getPlayer(), "&c&c&lERROR!&7 You cannot use that in the darkzone!");
	}

	public static World getTutorial() {
		return Bukkit.getWorld("tutorial");
	}

	public static World getDarkzone() {
		return Bukkit.getWorld("darkzone");
	}

	public static Location getDarkzoneSpawn() {
		return darkzoneSpawn;
	}

	public static Location getInitialDarkzoneSpawn() {
		return initialDarkzoneSpawn;
	}

	public static boolean inDarkzone(LivingEntity player) {
		return inDarkzone(player.getLocation());
	}

	public static boolean inDarkzone(Location location) {
		return location.getWorld() == getDarkzone();
	}
}
