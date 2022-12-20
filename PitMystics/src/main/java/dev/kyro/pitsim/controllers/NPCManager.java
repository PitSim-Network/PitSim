package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.*;

public class NPCManager implements Listener {
	public static List<PitNPC> pitNPCs = new ArrayList<>();
	public static Map<UUID, Long> cooldownPlayers = new HashMap<>();

	public static void onDisable() {
		for(PitNPC pitNPC : pitNPCs) pitNPC.remove();
	}

	public static void registerNPC(PitNPC pitNPC) {
		pitNPCs.add(pitNPC);
		Bukkit.getPluginManager().registerEvents(pitNPC, PitSim.INSTANCE);
	}

}
