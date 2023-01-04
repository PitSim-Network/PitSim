package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BossManager implements Listener {
	public static List<PitBoss> pitBosses = new ArrayList<>();


	/**
	 * Sets the damage the boss does
	 * @param attackEvent
	 */
	@EventHandler
	public static void onAttack(AttackEvent.Pre attackEvent) {
		PitBoss attackerBoss = getPitBoss(attackEvent.getAttacker());
		if(attackerBoss != null) {
			attackEvent.getEvent().setDamage(attackerBoss.getMeleeDamage());
		}
	}

	/**
	 * Called when the boss is attacked, saves the damage each player does to the boss in damageMap
	 * @param attackEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public static void onBossAttacked(AttackEvent.Apply attackEvent) {

		PitBoss defenderBoss = getPitBoss(attackEvent.getDefender());
		if(defenderBoss == null) return;
		Player player = attackEvent.getAttackerPlayer();
		if(!attackEvent.isAttackerPlayer()) return;

		UUID uuid = player.getUniqueId();
		defenderBoss.damageMap.put(uuid, defenderBoss.damageMap.getOrDefault(uuid, 0.0) + attackEvent.getEvent().getDamage());
	}

	/**
	 * Called when an entity is killed, checks if boss was killed, if so resets the sublevel to normal state and
	 * distrubutes rewards
	 * @param killEvent
	 */
	@EventHandler
	public static void onBossDeath(KillEvent killEvent) {
		PitBoss killedBoss = getPitBoss(killEvent.getDead());
		if(killedBoss == null) {
			return;
		}
		killedBoss.dropPool.distributeRewards(killedBoss.damageMap, 3);
		killedBoss.kill();
		killedBoss.getSubLevel().bossDeath();
	}

	/**
	 * Checks to see if entity is a boss
	 * @param entity
	 * @return true if entity is a boss, false if entity is not a boss
	 */
	public static boolean isBoss(LivingEntity entity) {
		return getPitBoss(entity) != null;
	}

	/**
	 * Returns pitBoss class if given entity is a pitBoss
	 * @param entity
	 * @return null if entity is not a pitBoss, else returns instance of pitBoss
	 */
	public static PitBoss getPitBoss(LivingEntity entity) {
		if(!(entity instanceof Player)) return null;
		for(PitBoss pitBoss : pitBosses) if(pitBoss.boss == entity) return pitBoss;
		return null;
	}
}
