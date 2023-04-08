package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

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
		if(attackEvent.getWrapperEvent().getSpigotEvent().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
				attackEvent.getWrapperEvent().hasAttackInfo()) return;
		PitBoss attackerBoss = getPitBoss(attackEvent.getAttacker());
		if(attackerBoss != null) {
			attackEvent.getWrapperEvent().getSpigotEvent().setDamage(attackerBoss.getDamage());
		}
	}

	@EventHandler
	public void onAttack2(AttackEvent.Apply attackEvent) {
		if(!isPitBoss(attackEvent.getDefender())) return;
		attackEvent.multipliers.add(1 / DarkzoneBalancing.SPOOFED_HEALTH_INCREASE);
	}

	/**
	 * Called when the boss is attacked, saves the damage each player does to the boss in damageMap
	 * @param attackEvent
	 */
//	No handler necessary
	public static void onAttack(AttackEvent.Apply attackEvent, double finalDamage) {
		PitBoss defenderBoss = getPitBoss(attackEvent.getDefender());
		if(defenderBoss == null) return;
		Player player = attackEvent.getAttackerPlayer();
		if(!attackEvent.isAttackerPlayer()) return;

		UUID uuid = player.getUniqueId();
		defenderBoss.getDamageMap().put(uuid, defenderBoss.getDamageMap().getOrDefault(uuid, 0.0) + finalDamage *
				DarkzoneBalancing.SPOOFED_HEALTH_INCREASE);
	}

	@EventHandler
	public static void onAttack(AttackEvent.Post attackEvent) {
		PitBoss defenderBoss = getPitBoss(attackEvent.getDefender());
		if(defenderBoss == null) return;
		defenderBoss.onHealthChange();
	}

	/**
	 * Checks to see if entity is a boss
	 * @param entity
	 * @return true if entity is a boss, false if entity is not a boss
	 */
	public static boolean isPitBoss(LivingEntity entity) {
		return getPitBoss(entity) != null;
	}

	/**
	 * Returns pitBoss class if given entity is a pitBoss
	 * @param entity
	 * @return null if entity is not a pitBoss, else returns instance of pitBoss
	 */
	public static PitBoss getPitBoss(LivingEntity entity) {
		if(!(entity instanceof Player)) return null;
		for(PitBoss pitBoss : pitBosses) if(pitBoss.getBoss() == entity) return pitBoss;
		return null;
	}
}
