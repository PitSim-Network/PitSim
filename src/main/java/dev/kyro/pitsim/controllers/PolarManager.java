package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AnticheatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.polar.api.event.DetectionAlertEvent;
import top.polar.api.event.MitigationEvent;
import top.polar.api.mitigation.MitigationType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PolarManager extends AnticheatManager implements Listener {

	public static Map<UUID, Long> bypassMap = new HashMap<>();

	@EventHandler
	public void onFlag(DetectionAlertEvent event) {
		if(event.getChatMessage().contains("Clicking suspiciously")) event.setCancelled(true);
	}

	@EventHandler
	public void onMitigate(MitigationEvent event) {
		if(event.getPlayer() == null) return;
		if(!bypassMap.containsKey(event.getPlayer().getUniqueId())) return;
		if(!event.getMitigationType().equals(MitigationType.MOVEMENT)) return;

		long time = bypassMap.get(event.getPlayer().getUniqueId());
		if(PitSim.currentTick > time) {
			bypassMap.remove(event.getPlayer().getUniqueId());
		} else {
			event.setCancelled(true);
		}
	}

	@Override
	public void exemptPlayer(Player player, long ticks, AnticheatManager.FlagType... flags) {
		bypassMap.put(player.getUniqueId(), PitSim.currentTick + ticks);
	}
}
