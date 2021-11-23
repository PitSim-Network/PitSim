package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
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
        if(event.getInventory().getType().equals(InventoryType.ENDER_CHEST) && !event.getPlayer().isOp()) {
            event.getPlayer().closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(event.getPlayer(), "pv 1");
                    if(event.getPlayer() instanceof Player) Sounds.ENDERCHEST_OPEN.play((Player) event.getPlayer());
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
        if(block.getType().equals(Material.ENDER_CHEST)) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(event.getPlayer(), "pv 1");
                    Sounds.ENDERCHEST_OPEN.play(event.getPlayer());
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);

        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("galacticvaults.openothers")) return;
        if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/pv") ||
                ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/playervault") ||
                ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/vault")) {
            Block block = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
            if(!block.getType().equals(Material.ENDER_CHEST)) {
                event.getPlayer().sendMessage("Unknown command. Type \"/help\" for help.");
                event.setCancelled(true);
            }
        }
    }
}
