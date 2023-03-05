package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.PlayerSpawnCommandEvent;
import dev.kyro.pitsim.events.WrapperEntityDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager implements Listener {
	public static int combatTime = 20 * 20;
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
		if(attackEvent.hasAttacker()) taggedPlayers.put(attackEvent.getAttacker().getUniqueId(), combatTime);
		taggedPlayers.put(attackEvent.getDefender().getUniqueId(), combatTime);

		if(attackEvent.isDefenderRealPlayer() && attackEvent.hasAttacker() &&
				attackEvent.getDefenderPitPlayer() != attackEvent.getAttacker()) {
			attackEvent.getDefenderPitPlayer().lastHitUUID = attackEvent.getAttacker().getUniqueId();
		}
	}

	@EventHandler
	public static void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.getPlayer().closeInventory();
		if(NonManager.getNon(event.getPlayer()) != null) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		UUID attackerUUID = pitPlayer.lastHitUUID;
		if(isInCombat(player) || pitPlayer.megastreak.isOnMega()) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(onlinePlayer.getUniqueId().equals(attackerUUID)) {

					Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
					Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
					EntityDamageByEntityEvent newEvent = new EntityDamageByEntityEvent(onlinePlayer, player, EntityDamageEvent.DamageCause.CUSTOM, 0);
					AttackEvent attackEvent = new AttackEvent(new WrapperEntityDamageEvent(newEvent), attackerEnchant, defenderEnchant, false);

					DamageManager.kill(attackEvent, onlinePlayer, player, KillType.KILL);
					return;
				}
			}
			DamageManager.death(player);
		}
		if(player.getWorld() == MapManager.getDarkzone() && !SpawnManager.isInDarkzoneSpawn(player.getLocation())) {
			DamageManager.death(player);
		}
	}

	@EventHandler
	public static void onKill(KillEvent event) {
		taggedPlayers.remove(event.getDead().getUniqueId());
		if(event.isDeadPlayer()) event.getDeadPitPlayer().lastHitUUID = null;
	}

	@EventHandler
	public void onSpawn(PlayerSpawnCommandEvent event) {
		Player player = event.getPlayer();
		if(taggedPlayers.containsKey(player.getUniqueId())) {
			event.setCancelled(true);
			AOutput.error(player, "&c&lERROR!&7 You cannot use that while in combat!");
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.isOnMega()) {
			event.setCancelled(true);
			AOutput.send(player, "&c&lERROR!&7 You cannot spawn while on a megastreak");
			return;
		}
	}
}
