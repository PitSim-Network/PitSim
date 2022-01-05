package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitMap;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.pitmaps.BiomesMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapManager implements Listener {
	public static List<PitMap> mapList = new ArrayList<>();
	public static PitMap currentMap;

	public static boolean multiLobbies = false;
	public static int ENABLE_THRESHOLD = 10;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				disableMultiLobbies();
			}
		}.runTaskLater(PitSim.INSTANCE, 20L);
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				count++;

				int players = AFKManager.onlineActivePlayers;
				boolean chaos = BoosterManager.getBooster("chaos").minutes > 0;
				if(multiLobbies) {
					if(chaos) return;
					if(count % (60 * 10) == 0 && players < ENABLE_THRESHOLD) disableMultiLobbies();
				} else {
					if(players >= ENABLE_THRESHOLD || chaos) enableMultiLobbies();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public static void onStart() {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.teleport(currentMap.getSpawn(currentMap.firstLobby));
		}
	}

	public static void registerMap(PitMap pitMap) {
		mapList.add(pitMap);
		currentMap = pitMap;
	}

	public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);

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

	public static void enableMultiLobbies() {
		if(multiLobbies) return;
		multiLobbies = true;
		for(World lobby : currentMap.lobbies) {
			enablePortal(lobby);
		}
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			AOutput.send(onlinePlayer, "&6&lLobby! &7Second lobby enabled");
		}
	}

	public static void disableMultiLobbies() {
		if(!multiLobbies) return;
		multiLobbies = false;
		List<World> disabledLobbies = new ArrayList<>(currentMap.lobbies);
		disabledLobbies.remove(0);
		disablePortal(currentMap.firstLobby);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!disabledLobbies.contains(onlinePlayer.getWorld())) continue;
			AOutput.send(onlinePlayer, "&6&lLOBBY! &7Instance shutdown... Please make your way to the exit portal");
		}
	}

	public static void enablePortal(World lobby) {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/doorOpen.schematic"), new Location(lobby, -67, 72, 3));
	}

	public static void disablePortal(World lobby) {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/doorClosed.schematic"), new Location(lobby, -67, 72, 3));
	}
}
