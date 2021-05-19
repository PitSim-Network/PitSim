package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager implements Listener {

	public static List<UUID> swapCooldown = new ArrayList<>();

	@EventHandler
	public static void onClick(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(player.getItemInHand()) || !player.getItemInHand().getType().toString().contains("LEGGINGS")) return;

		if(swapCooldown.contains(player.getUniqueId())) {

			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		ItemStack held = player.getItemInHand();
		player.setItemInHand(player.getInventory().getLeggings());
		player.getInventory().setLeggings(held);

		swapCooldown.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				swapCooldown.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 40L);

		ASound.play(player, Sound.HORSE_ARMOR, 1F, 1.3F);
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {

				player.setMaxHealth(32);
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}
}
