package net.pitsim.spigot.controllers;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.PitJoinEvent;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbySwitchManager implements Listener {

	public static List<Player> recentlyJoined = new ArrayList<>();
	public static List<Player> switchingPlayers = new ArrayList<>();
	public static List<UUID> joinedFromDarkzone = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if(switchingPlayers.contains((Player) event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickup(PlayerPickupItemEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandSend(PlayerCommandPreprocessEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSneak(PlayerToggleSneakEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHit(AttackEvent.Pre event) {
		if(switchingPlayers.contains(event.getDefenderPlayer()) || switchingPlayers.contains(event.getAttackerPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVanillaHit(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(switchingPlayers.contains((Player) event.getEntity())) event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		recentlyJoined.add(event.getPlayer());

		new BukkitRunnable() {
			@Override
			public void run() {
				recentlyJoined.remove(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 5);
	}

	public static void setSwitchingPlayer(Player player) {

		switchingPlayers.add(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 40, 100, false, false);
				player.closeInventory();
			}
		}.runTask(PitSim.INSTANCE);

//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				switchingPlayers.remove(player);
//			}
//		}.runTaskLater(PitSim.INSTANCE, 40 + 5);
	}

	public static void removeSwitchingPlayer(Player player) {
		switchingPlayers.remove(player);
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		switchingPlayers.remove(event.getPlayer());
	}

}
