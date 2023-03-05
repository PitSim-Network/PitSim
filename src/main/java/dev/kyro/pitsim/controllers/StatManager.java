package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.HoursPlayedQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
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
					if(AFKManager.AFKPlayers.contains(player)) continue;
					if(VanishAPI.isInvisible(player)) continue;
					pitPlayer.stats.minutesPlayed++;
					HoursPlayedQuest.INSTANCE.progressTime(pitPlayer);
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
		if(pitPlayer.stats == null) return;

		pitPlayer.stats.deaths++;
		if(pitPlayer.megastreak.getClass() != NoMegastreak.class) {
			pitPlayer.stats.deaths++;
			if(pitPlayer.getKills() > pitPlayer.stats.highestStreak)
				pitPlayer.stats.highestStreak = pitPlayer.getKills();
		}
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
			pitAttacker.stats.damageDealt += attackEvent.getFinalDamage();
			pitAttacker.stats.trueDamageDealt += attackEvent.trueDamage + attackEvent.veryTrueDamage;
			pitAttacker.stats.trueDamageTaken += attackEvent.selfTrueDamage + attackEvent.selfVeryTrueDamage;

			if(Regularity.isRegHit(attackEvent.getDefender())) pitAttacker.stats.regularity++;
		}

		if(pitDefender.stats != null) {
			pitDefender.stats.damageTaken += attackEvent.getFinalDamage();
			pitDefender.stats.trueDamageTaken += attackEvent.trueDamage + attackEvent.veryTrueDamage;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(KillEvent killEvent) {
		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.getKillerPlayer());
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.getDeadPlayer());

		if(pitKiller != null) {
			if(pitKiller.stats != null) {
				if(HopperManager.isHopper(killEvent.getDead())) {
					pitKiller.stats.hopperKills++;
				} else if(PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) {
					pitKiller.stats.playerKills++;
				} else {
					pitKiller.stats.botKills++;
				}

				pitKiller.stats.totalGold += killEvent.getFinalGold();
			}
		}

		if(pitDead != null && pitDead.stats != null && pitDead.megastreak.getClass() != NoMegastreak.class) {
			pitDead.stats.deaths++;
			if(pitDead.getKills() > pitDead.stats.highestStreak) pitDead.stats.highestStreak = pitDead.getKills();
		}
	}
}
