package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitMap;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.SchematicPaste;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapManager implements Listener {
	public static List<PitMap> mapList = new ArrayList<>();
	public static PitMap currentMap;

	public static int ENABLE_THRESHOLD = 10;

	public static void openPortal() {
		enablePortal(currentMap.world);

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.teleport(currentMap.getSpawn());
		}
	}

	public static void registerMap(PitMap pitMap) {
		mapList.add(pitMap);
		currentMap = pitMap;
	}

	public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);

	public static void enablePortal(World lobby) {
		SchematicPaste.loadSchematic(new File("plugins/WorldEdit/schematics/doorOpen.schematic"), new Location(lobby, -67, 72, 3));
	}

	public static World getTutorial() {
		return Bukkit.getWorld("tutorial");
	}

	public static World getDarkzone() {
		return Bukkit.getWorld("darkzone");
	}

	public static boolean inDarkzone(LivingEntity player) {
		return inDarkzone(player.getLocation());
	}

	public static boolean inDarkzone(Location location) {
		return location.getWorld() == getDarkzone();
	}
}
