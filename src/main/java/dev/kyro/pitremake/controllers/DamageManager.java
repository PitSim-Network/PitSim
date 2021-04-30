package dev.kyro.pitremake.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.Map;

public class DamageManager implements Listener {

	public static Map<EntityShootBowEvent, Map<PitEnchant, Integer>> arrowMap = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	@EventHandler
	public void onArrowLand(ProjectileHitEvent event) {

		if(!(event.getEntity() instanceof Arrow)) return;
		for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {
			if(!entry.getKey().getEntity().equals(event.getEntity())) continue;
			arrowMap.remove(entry.getKey());
			return;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player) || (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow))) return;
		if(event.getDamager() instanceof Player) {

			handleAttack(new DamageEvent(event, EnchantManager.getEnchantsOnPlayer((Player) event.getDamager())));
		} else {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getEntity().equals(event.getDamager())) continue;
				handleAttack(new DamageEvent(event, arrowMap.remove(entry.getKey())));
			}
		}
	}

	public static void handleAttack(DamageEvent damageEvent) {

		AOutput.send(damageEvent.attacker, "Initial Damage: " + damageEvent.event.getDamage());

		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
			pitEnchant.onDamage(damageEvent);
		}

		double damage = getFinalDamage(damageEvent);

		damageEvent.event.setDamage(damage);

		AOutput.send(damageEvent.attacker, "Final Damage: " + damageEvent.event.getDamage());

		if(damageEvent.trueDamage != 0) {
			double finalHealth = damageEvent.defender.getHealth() - damageEvent.trueDamage;
			if(finalHealth <= 0) {
//				TODO: Call death
			} else {
				damageEvent.defender.setHealth(finalHealth);
			}
		}

		if(damageEvent.selfTrueDamage != 0) {
			double finalHealth = damageEvent.attacker.getHealth() - damageEvent.selfTrueDamage;
			if(finalHealth <= 0) {
//				TODO: Call death
			} else {
				damageEvent.attacker.setHealth(finalHealth);
				damageEvent.attacker.damage(0);
			}
		}
	}

	public static double getFinalDamage(DamageEvent damageEvent) {

		double damage = damageEvent.event.getDamage();
		damage += damageEvent.increase;
		damage *= 1 + damageEvent.increasePercent;
		for(double multiplier : damageEvent.multiplier) {
			damage *= multiplier;
		}
		damage *= 1 - damageEvent.decreasePercent;
		damage -= damageEvent.decrease;
		damage = Math.max(damage, 0);

		return damage;
	}
}
