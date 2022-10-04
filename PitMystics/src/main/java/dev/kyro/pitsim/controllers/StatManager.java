package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StatManager implements Listener {
	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					if(!AFKManager.AFKPlayers.contains(player)) pitPlayer.stats.minutesPlayed++;
//					pitPlayer.stats.save();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 20 * 60L);
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
		if(!attackEvent.isAttackerPlayer() || !attackEvent.isDefenderPlayer()) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();

		if(pitAttacker.stats != null) {
			if(attackEvent.getPet() == null) {
				if(attackEvent.getArrow() == null) pitAttacker.stats.swordHits++;
				else pitAttacker.stats.arrowHits++;
			}
			pitAttacker.stats.damageDealt += attackEvent.getEvent().getFinalDamage();
			pitAttacker.stats.trueDamageDealt += attackEvent.trueDamage + attackEvent.veryTrueDamage;
			pitAttacker.stats.trueDamageTaken += attackEvent.selfTrueDamage + attackEvent.selfVeryTrueDamage;

			if(Regularity.isRegHit(attackEvent.getDefender())) pitAttacker.stats.regularity++;
		}

		if(pitDefender.stats != null) {
			pitDefender.stats.damageTaken += attackEvent.getEvent().getFinalDamage();
			pitDefender.stats.trueDamageTaken += attackEvent.trueDamage + attackEvent.veryTrueDamage;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;
		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		PitPlayer pitDead = killEvent.getDeadPitPlayer();

		if(pitKiller.stats != null) {
			if(HopperManager.isHopper(killEvent.getDead())) {
				pitKiller.stats.hopperKills++;
			} else if(NonManager.getNon(killEvent.getDead()) == null) {
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
