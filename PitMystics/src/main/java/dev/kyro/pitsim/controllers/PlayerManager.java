package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager implements Listener {

	public static List<UUID> swapCooldown = new ArrayList<>();

	@EventHandler
	public static void onKill(KillEvent killEvent) {

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.dead);
		Non killingNon = NonManager.getNon(killEvent.killer);
		Non deadNon = NonManager.getNon(killEvent.dead);

		if(pitDead.bounty != 0 && killingNon == null) {
			DecimalFormat formatter = new DecimalFormat("#,###.#");
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
					"&6&lBOUNTY CLAIMED!&7 " + killEvent.killer.getDisplayName() + "&7 killed " + killEvent.dead.getDisplayName() +
							"&7 for &6&l" + formatter.format(pitDead.bounty)) + "g");
			PitSim.VAULT.depositPlayer(killEvent.killer, pitDead.bounty);
			pitDead.bounty = 0;
		}

		if(Math.random() < 0.1 && killingNon == null) {

			int amount = (int) Math.floor(Math.random() * 5 + 1) * 5000;
			pitKiller.bounty += amount;
			AOutput.send(killEvent.killer, "&6&lBOUNTY!&7 bump &6&l" + amount + "g&7 on " + killEvent.killer.getDisplayName() +
					"&7 for high streak");
			ASound.play(killEvent.killer, Sound.WITHER_SPAWN, 1, 1);
		}
	}

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

				if(!player.isOp()) {

					int itemsRemoved = 0;
					for(int i = 0; i < 36; i++) {

						ItemStack itemStack = player.getInventory().getItem(i);
						if(EnchantManager.isIllegalItem(itemStack)) {
							player.getInventory().setItem(i, new ItemStack(Material.AIR));
							itemsRemoved++;
						}
					}
					if(EnchantManager.isIllegalItem(player.getEquipment().getLeggings())) {
						player.getEquipment().setLeggings(new ItemStack(Material.AIR));
						itemsRemoved++;
					}
					if(itemsRemoved != 0) AOutput.error(player, "&c" + itemsRemoved + " &7illegal item" +
							(itemsRemoved == 1 ? " was" : "s were") + " removed from your inventory");
				}

				player.setMaxHealth(28);
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}
}
