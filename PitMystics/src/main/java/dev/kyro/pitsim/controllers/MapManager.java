package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitMap;
import dev.kyro.pitsim.pitmaps.BiomesMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements Listener {
	public static List<PitMap> mapList = new ArrayList<>();
	public static PitMap currentMap;

	public static void onStart() {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.teleport(currentMap.getSpawn(currentMap.firstLobby));
		}
	}

	public static void registerMap(PitMap pitMap) {
		mapList.add(pitMap);
		currentMap = pitMap;
	}

	public static Location playerDesert = new org.bukkit.Location(Bukkit.getWorld("pit"), -108, 86, 194, 48, 3);
	public static Location desertNonSpawn = new Location(Bukkit.getWorld("pit"), -119, 85, 205);
	public static Location desertMid = new Location(Bukkit.getWorld("pit"), -118, 43, 204);
	public static int desertY = 42;

	public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);
	public static Location snowNonSpawn = new Location(Bukkit.getWorld("pit"), -99, 46, 716, -90, 0);
	public static Location snowMid = new Location(Bukkit.getWorld("pit"), -98, 6, 716);
	public static int snowY = 4;

	@EventHandler
	public void onMove(PlayerPortalEvent event) {
		if(currentMap.getClass() != BiomesMap.class || event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		Location playerLoc = player.getLocation();

		Location teleportLoc = playerLoc.clone();
		teleportLoc.setWorld(MapManager.currentMap.getRandomOrFirst(player.getWorld()));
		if(teleportLoc.getYaw() > 0 || teleportLoc.getYaw() < -180) teleportLoc.setYaw(-teleportLoc.getYaw());
		teleportLoc.add(3, 0, 0);
		teleportLoc.setY(72);

		player.teleport(teleportLoc);
		player.setVelocity(new Vector(1.5, 1, 0));
		AOutput.send(player, "&7You have connected to lobby &6" + (MapManager.currentMap.getLobbyIndex(teleportLoc.getWorld()) + 1));
	}
}
