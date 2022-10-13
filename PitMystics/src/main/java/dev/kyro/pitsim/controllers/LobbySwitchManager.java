package dev.kyro.pitsim.controllers;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbySwitchManager implements Listener {

	public static List<Player> recentlyJoined = new ArrayList<>();
	public static List<Player> switchingPlayers = new ArrayList<>();
	public static List<UUID> joinedFromDarkzone = new ArrayList<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if(switchingPlayers.contains((Player) event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent event) {
		if(switchingPlayers.contains(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		recentlyJoined.add(event.getPlayer());

		new BukkitRunnable() {
			@Override
			public void run() {
				recentlyJoined.remove(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 5);
	}

	public static void setSwitchingPlayer(Player player) {
		player.closeInventory();

		switchingPlayers.add(player);
		Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 40, 100, false, false);

		new BukkitRunnable() {
			@Override
			public void run() {
				switchingPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 40);
	}

}
