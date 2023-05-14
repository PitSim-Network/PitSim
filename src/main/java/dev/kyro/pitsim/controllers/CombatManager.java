package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import dev.kyro.pitsim.events.PlayerSpawnCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager implements Listener {
	public static HashMap<UUID, Integer> taggedPlayers = new HashMap<>();

	public static int getCombatTicks() {
		return PitSim.status.isOverworld() ? 20 * 20 : 20 * 5;
	}

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
		if(attackEvent.hasAttacker()) taggedPlayers.put(attackEvent.getAttacker().getUniqueId(), getCombatTicks());
		taggedPlayers.put(attackEvent.getDefender().getUniqueId(), getCombatTicks());

		if(attackEvent.isDefenderRealPlayer() && attackEvent.hasAttacker() &&
				attackEvent.getDefenderPitPlayer() != attackEvent.getAttacker()) {
			attackEvent.getDefenderPitPlayer().lastHitUUID = attackEvent.getAttacker().getUniqueId();
		}
	}

	@EventHandler
	public static void onLeave(PitQuitEvent event) {
		Player player = event.getPlayer();
		event.getPlayer().closeInventory();
		if(NonManager.getNon(event.getPlayer()) != null) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(isInCombat(player) || pitPlayer.isOnMega()) DamageManager.killPlayer(player);
		if(player.getWorld() == MapManager.getDarkzone() && !SpawnManager.isInSpawn(player)) DamageManager.killPlayer(player);
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
		if(pitPlayer.isOnMega()) {
			event.setCancelled(true);
			AOutput.send(player, "&c&lERROR!&7 You cannot spawn while on a megastreak");
			return;
		}
	}
}
