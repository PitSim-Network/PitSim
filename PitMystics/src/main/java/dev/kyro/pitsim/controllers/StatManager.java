package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StatManager implements Listener {
	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					if(!AFKManager.AFKPlayers.contains(player)) pitPlayer.stats.minutesPlayed++;
					pitPlayer.stats.save();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20 * 60L);
	}

	@EventHandler
	public void saveStats(PlayerQuitEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		pitPlayer.stats.save();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMessage(AsyncPlayerChatEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.stats != null) pitPlayer.stats.chatMessages++;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer((Player) event.getEntity());
		if(pitPlayer.stats != null) pitPlayer.stats.arrowShots++;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onOof(OofEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.stats != null) pitPlayer.stats.deaths++;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHeal(HealEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.stats == null) return;
		if(event.healType == HealEvent.HealType.HEALTH) {
			pitPlayer.stats.healthRegained += event.getEffectiveHeal();
		} else {
			pitPlayer.stats.absorptionGained += event.getEffectiveHeal();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAttack(AttackEvent.Apply attackEvent) {
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);

		if(pitAttacker.stats != null) {
			if(attackEvent.pet == null) {
				if(attackEvent.arrow == null) pitAttacker.stats.swordHits++; else pitAttacker.stats.arrowHits++;
			}
			pitAttacker.stats.damageDealt += attackEvent.event.getFinalDamage();
			pitAttacker.stats.trueDamageDealt += attackEvent.trueDamage + attackEvent.veryTrueDamage;
			pitAttacker.stats.trueDamageTaken += attackEvent.selfTrueDamage + attackEvent.selfVeryTrueDamage;

			if(Regularity.isRegHit(attackEvent.defender)) pitAttacker.stats.regularity++;
		}

		if(pitDefender.stats != null) {
			pitDefender.stats.damageTaken += attackEvent.event.getFinalDamage();
			pitDefender.stats.trueDamageTaken += attackEvent.trueDamage + attackEvent.veryTrueDamage;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(KillEvent killEvent) {
		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.dead);

		if(pitKiller.stats != null) {
			if(HopperManager.isHopper(killEvent.dead)) {
				pitKiller.stats.hopperKills++;
			} else if(NonManager.getNon(killEvent.dead) == null) {
				pitKiller.stats.playerKills++;
			} else {
				pitKiller.stats.botKills++;
			}

			pitKiller.stats.totalGold += killEvent.getFinalGold();
		}

		if(pitDead.stats != null) {
			pitDead.stats.deaths++;
			if(pitDead.getKills() > pitDead.stats.highestStreak) pitDead.stats.highestStreak = (int) pitDead.getKills();
		}
	}
}
