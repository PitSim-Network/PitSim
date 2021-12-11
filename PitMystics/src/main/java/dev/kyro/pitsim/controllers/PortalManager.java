package dev.kyro.pitsim.controllers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalManager {

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.getPlayer().teleport(Bukkit.getWorld("ffa").getSpawnLocation());
	}
}
