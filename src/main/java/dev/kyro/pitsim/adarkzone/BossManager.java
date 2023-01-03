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

	@EventHandler
	public static void onAttack(AttackEvent.Pre attackEvent) {
		PitBoss attackerBoss = getPitBoss(attackEvent.getAttacker());
		if(attackerBoss != null) {
			attackEvent.getEvent().setDamage(attackerBoss.getMeleeDamage());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void onBossAttacked(AttackEvent.Apply attackEvent) {

		PitBoss defenderBoss = getPitBoss(attackEvent.getDefender());
		if(defenderBoss == null) return;
		Player player = attackEvent.getAttackerPlayer();
		if(!attackEvent.isAttackerPlayer()) return;

		UUID uuid = player.getUniqueId();
		defenderBoss.damageMap.put(uuid, defenderBoss.damageMap.getOrDefault(uuid, 0.0) + attackEvent.getEvent().getDamage());
	}

	@EventHandler
	public static void onBossDeath(KillEvent killEvent) {
		PitBoss killedBoss = getPitBoss(killEvent.getDead());
		if(killedBoss == null) {
			return;
		}
		killedBoss.dropPool.distributeRewards(killedBoss.damageMap);
		killedBoss.kill();
		killedBoss.getSubLevel().bossDeath();
	}

	public static boolean isBoss(LivingEntity entity) {
		return getPitBoss(entity) != null;
	}

	public static PitBoss getPitBoss(LivingEntity entity) {
		if(!(entity instanceof Player)) return null;
		for(PitBoss pitBoss : pitBosses) if(pitBoss.boss == entity) return pitBoss;
		return null;
	}
}
