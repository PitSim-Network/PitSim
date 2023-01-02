package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

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
	public static void onAttack(AttackEvent.Apply attackEvent) {
//		attackEvent.getFinalDamage()

		PitBoss defenderBoss = getPitBoss(attackEvent.getDefender());
		if(defenderBoss != null) {

		}
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
