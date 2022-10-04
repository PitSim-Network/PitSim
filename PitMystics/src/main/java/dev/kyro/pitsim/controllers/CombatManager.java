package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
		LivingEntity attacker = attackEvent.getAttacker();
		LivingEntity defender = attackEvent.getDefender();

		taggedPlayers.put(attacker.getUniqueId(), combatTime);
		taggedPlayers.put(defender.getUniqueId(), combatTime);
	}

	@EventHandler
	public static void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
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

					DamageManager.kill(attackEvent, onlinePlayer, player, false, KillType.DEFAULT);
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
	public static void onDeath(KillEvent event) {
		taggedPlayers.remove(event.getDead().getUniqueId());
		if(event.isDeadPlayer()) event.getDeadPitPlayer().lastHitUUID = null;
	}

	@EventHandler
	public static void onOof(OofEvent event) {
		taggedPlayers.remove(event.getPlayer().getUniqueId());
		PitPlayer.getPitPlayer(event.getPlayer()).lastHitUUID = null;
	}

	@EventHandler
	public static void onCommandSend(PlayerCommandPreprocessEvent event) {
		List<String> blockedCommands = new ArrayList<>();
		blockedCommands.add("/ec");
		blockedCommands.add("/echest");
		blockedCommands.add("/enderchest");
		blockedCommands.add("/perks");
		blockedCommands.add("/spawn");

		if(taggedPlayers.containsKey(event.getPlayer().getUniqueId())) {
			for(String cmd : blockedCommands) {
				if(cmd.equalsIgnoreCase(event.getMessage())) {
					event.setCancelled(true);
					AOutput.error(event.getPlayer(), "&c&c&lNOPE! &7You cannot use that while in combat!");
				}
			}
		}
	}
}
