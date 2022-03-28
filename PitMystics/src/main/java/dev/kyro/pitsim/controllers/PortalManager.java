package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class PortalManager implements Listener {

	@EventHandler
	public void onPortal(EntityPortalEvent event) {
		if(event.getEntity() instanceof Player) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		Location playerLoc = player.getLocation();

		Location teleportLoc;
		if(player.getWorld() != Bukkit.getWorld("darkzone")) {
			teleportLoc = playerLoc.clone().add(59, -1, -97);
			teleportLoc.setWorld(Bukkit.getWorld("darkzone"));
		}
		else {
			teleportLoc = playerLoc.clone().add(-59, 1, 97);
			teleportLoc.setWorld(Bukkit.getWorld("biomes1"));
		}


		if(teleportLoc.getYaw() > 0 || teleportLoc.getYaw() < -180) teleportLoc.setYaw(-teleportLoc.getYaw());
		teleportLoc.add(3, 0, 0);
		teleportLoc.setY(72);

		player.teleport(teleportLoc);
		player.setVelocity(new Vector(1.5, 1, 0));
		if(player.getWorld() == Bukkit.getWorld("darkzone")) AOutput.send(player, "&7You have been sent to the &d&k||&5&lDarkzone&d&k||&7.");
		else AOutput.send(player, "&7You have been sent to the &a&lOverworld&7.");
	}
}
