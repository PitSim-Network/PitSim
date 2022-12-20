package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

	//TODO: Fix this mess

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(NPCRightClickEvent event) {

		if(cooldownPlayers.containsKey(event.getClicker().getUniqueId())) {
			System.out.println("Canceled");
			event.setCancelled(true);
		} else {
			cooldownPlayers.put(event.getClicker().getUniqueId(), System.currentTimeMillis());
		}
		long time = cooldownPlayers.get(event.getClicker().getUniqueId());

		if(2 * 1000 + time < System.currentTimeMillis()) cooldownPlayers.remove(event.getClicker().getUniqueId());
	}
}
