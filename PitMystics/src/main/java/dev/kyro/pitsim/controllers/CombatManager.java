package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.events.PlayerSpawnCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager implements Listener {
	int combatTime = 20 * 20;
	public static HashMap<UUID, Integer> taggedPlayers = new HashMap<>();

	public static boolean isInCombat(Player player) {
		return taggedPlayers.containsKey(player.getUniqueId());
	}

	static {

		new BukkitRunnable() {
			@Override
			public void run() {

				List<UUID> toRemove = new ArrayList<>();
				for(Map.Entry<UUID, Integer> entry : taggedPlayers.entrySet()) {
					int time = entry.getValue();
					time = time - 1;

					if(time > 0) taggedPlayers.put(entry.getKey(), time);
					else toRemove.add(entry.getKey());
				}

				for(UUID uuid : toRemove) {
					taggedPlayers.remove(uuid);
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(player.getUniqueId().equals(uuid)) {
							PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
							pitPlayer.lastHitUUID = null;
						}
					}
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		LivingEntity attacker = attackEvent.attacker;
		LivingEntity defender = attackEvent.defender;

		taggedPlayers.put(attacker.getUniqueId(), combatTime);
		taggedPlayers.put(defender.getUniqueId(), combatTime);

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onJoin(PlayerJoinEvent event) {


	}

	@EventHandler
	public static void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitplayer = PitPlayer.getPitPlayer(event.getPlayer());
		event.getPlayer().closeInventory();
		if(NonManager.getNon(event.getPlayer()) != null) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		UUID attackerUUID = pitPlayer.lastHitUUID;
		if(taggedPlayers.containsKey(player.getUniqueId())) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(onlinePlayer.getUniqueId().equals(attackerUUID)) {

					Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
					Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
					EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(onlinePlayer, player, EntityDamageEvent.DamageCause.CUSTOM, 0);
					AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

					DamageManager.kill(attackEvent, onlinePlayer, player, KillType.DEFAULT);
					return;
				}
			}
			DamageManager.death(player);
		}
		if(player.getWorld() == MapManager.getDarkzone() && !SpawnManager.isInDarkzoneSpawn(player.getLocation())) {
			DamageManager.death(player);
		}

//        Player player = event.getPlayer();
//
//        if(taggedPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("pitsim.combatlog") && !player.isOp()) {
//            player.teleport(Bukkit.getWorld("pit").getSpawnLocation());
//            taggedPlayers.remove(player.getUniqueId());
//
//            bannedPlayers.add(player.getUniqueId());
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    bannedPlayers.remove(player.getUniqueId());
//                }
//            }.runTaskLater(PitSim.INSTANCE, 60 * 20);
//        }
	}

	@EventHandler
	public static void onDeath(KillEvent event) {
		taggedPlayers.remove(event.dead.getUniqueId());
		if(event.deadIsPlayer) PitPlayer.getPitPlayer(event.deadPlayer).lastHitUUID = null;
	}

	@EventHandler
	public static void onOof(OofEvent event) {
		taggedPlayers.remove(event.getPlayer().getUniqueId());
		PitPlayer.getPitPlayer(event.getPlayer()).lastHitUUID = null;
	}

	@EventHandler
	public void onSpawn(PlayerSpawnCommandEvent event) {
		Player player = event.getPlayer();
		if(!taggedPlayers.containsKey(player.getUniqueId())) return;
		event.setCancelled(true);
		AOutput.error(event.getPlayer(), "&c&c&lNOPE! &7You cannot use that while in combat!");
	}
}
