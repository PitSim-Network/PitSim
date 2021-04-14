package dev.kyro.pitremake.controllers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageManager implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
		Player attacker = (Player) event.getDamager();
		Player defender = (Player) event.getEntity();

		handleAttack(new DamageEvent(event));
	}

	public void handleAttack(DamageEvent damageEvent) {

		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
			pitEnchant.onDamage(damageEvent);
		}

		double damage = damageEvent.event.getDamage();
		damage += damageEvent.increase;
		damage *= 1 + damageEvent.increasePercent;
		for(double multiplier : damageEvent.multiplier) {
			damage *= multiplier;
		}
		damage *= 1 - damageEvent.decreasePercent;
		damage -= damageEvent.decrease;
		damage = Math.max(damage, 0);

		damageEvent.event.setDamage(damage);

		double finalHealth = damageEvent.defender.getHealth() - damageEvent.trueDamage;
		if(finalHealth <= 0) {
//			TODO: Call death
			return;
		}
		damageEvent.defender.setHealth(finalHealth);

		finalHealth = damageEvent.attacker.getHealth() - damageEvent.selfTrueDamage;
		if(finalHealth <= 0) {
//			TODO: Call death
			return;
		}
		damageEvent.attacker.setHealth(finalHealth);
		damageEvent.attacker.damage(0);
	}
}
