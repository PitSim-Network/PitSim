package dev.kyro.pitsim.holograms;

import dev.kyro.pitsim.events.PitJoinEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class HologramManager implements Listener {

	public static List<Hologram> holograms = new ArrayList<>();

	public static void registerHologram(Hologram hologram) {
		holograms.add(hologram);

		if(hologram.viewMode != ViewMode.ALL) return;
		hologram.setPermittedViewers(new ArrayList<>(Bukkit.getOnlinePlayers()));
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		for(Hologram hologram : holograms) {
			if(hologram.viewMode != ViewMode.ALL) continue;

			hologram.addPermittedViewer(event.getPlayer());
		}
	}

	@EventHandler
	public void onLeave(PitQuitEvent event) {
		for(Hologram hologram : holograms) {
			if(hologram.viewMode != ViewMode.ALL) continue;

			hologram.removePermittedViewer(event.getPlayer());
		}
	}
}
