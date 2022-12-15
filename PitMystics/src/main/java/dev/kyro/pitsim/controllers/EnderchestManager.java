package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.storage.EnderchestGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class EnderchestManager implements Listener {

	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		if(MapManager.inDarkzone(event.getPlayer())) return;
		if(event.getInventory().getType().equals(InventoryType.ENDER_CHEST) && !event.getPlayer().isOp()) {
			event.getPlayer().closeInventory();
			if(ShutdownManager.enderchestDisabled) {
				AOutput.error(event.getPlayer(), "&cYou may not open the Enderchest right now.");
				event.setCancelled(true);
				return;
			}

			EnderchestGUI gui = new EnderchestGUI((Player) event.getPlayer(), event.getPlayer().getUniqueId());
			gui.open();
			if(event.getPlayer() instanceof Player) Sounds.ENDERCHEST_OPEN.play(event.getPlayer());

			new BukkitRunnable() {
					@Override
					public void run() {
						EnderchestGUI gui = new EnderchestGUI((Player) event.getPlayer(), event.getPlayer().getUniqueId());
						gui.open();
						Sounds.ENDERCHEST_OPEN.play(event.getPlayer());
					}
				}.runTaskLater(PitSim.INSTANCE, 1L);

		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(MapManager.inDarkzone(event.getPlayer())) return;
		try {
			Block block = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
			if(block.getType().equals(Material.ENDER_CHEST)) {
				if(ShutdownManager.enderchestDisabled) {
					AOutput.error(event.getPlayer(), "&cYou may not open the Enderchest right now.");
					event.setCancelled(true);
					return;
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						EnderchestGUI gui = new EnderchestGUI(event.getPlayer(), event.getPlayer().getUniqueId());
						gui.open();
						Sounds.ENDERCHEST_OPEN.play(event.getPlayer());
					}
				}.runTaskLater(PitSim.INSTANCE, 1L);

			}
		} catch (Exception ignored) { };
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		if(player.isOp() || player.hasPermission("galacticvaults.openothers")) return;
		if(ChatColor.stripColor(message).toLowerCase().startsWith("/pv") ||
				ChatColor.stripColor(message).toLowerCase().startsWith("/playervault") ||
				ChatColor.stripColor(message).toLowerCase().startsWith("/vault")) {
			Block block = player.getTargetBlock((HashSet<Byte>) null, 5);
			if(!block.getType().equals(Material.ENDER_CHEST) || ShutdownManager.enderchestDisabled || MapManager.inDarkzone(player)) {
				event.setCancelled(true);
				AOutput.error(player, "&c&lERROR!&7 You cannot do this right now!");
			}
		}
	}
}
