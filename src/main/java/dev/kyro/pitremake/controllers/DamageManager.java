package dev.kyro.pitremake.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.misc.Misc;
import dev.kyro.pitremake.nons.Non;
import dev.kyro.pitremake.nons.NonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageManager implements Listener {

	public static List<Player> hitCooldownList = new ArrayList<>();
	public static Map<EntityShootBowEvent, Map<PitEnchant, Integer>> arrowMap = new HashMap<>();

	static {

		new BukkitRunnable() {
			@Override
			public void run() {

				List<EntityShootBowEvent> toRemove = new ArrayList<>();
				for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

					if(entry.getKey().getProjectile().isDead()) toRemove.add(entry.getKey());
				}
				for(EntityShootBowEvent remove : toRemove) {
					arrowMap.remove(remove);
				}
			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player) || (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow))) return;
		Player attacker = (Player) event.getDamager();
		Player defender = (Player) event.getEntity();

		if(hitCooldownList.contains((Player) event.getEntity())) {
			event.setCancelled(true);
			return;
		}
		DamageManager.hitCooldownList.add(defender);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.hitCooldownList.remove(defender);
			}
		}.runTaskLater(PitRemake.INSTANCE, 10L);

		if(event.getDamager() instanceof Player) {

			handleAttack(new DamageEvent(event, EnchantManager.getEnchantsOnPlayer((Player) event.getDamager())));
		} else {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				handleAttack(new DamageEvent(event, arrowMap.get(entry.getKey())));
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
//			if(finalHealth <= 0) {
//				TODO: Call death
//			} else {
				damageEvent.defender.setHealth(Math.max(finalHealth, 0));
//			}
		}

		if(damageEvent.selfTrueDamage != 0) {
			double finalHealth = damageEvent.attacker.getHealth() - damageEvent.selfTrueDamage;
//			if(finalHealth <= 0) {
//				TODO: Call death
//			} else {
				damageEvent.attacker.setHealth(finalHealth);
				damageEvent.attacker.damage(Math.max(finalHealth, 0));
//			}
		}

		if(damageEvent.event.getFinalDamage() >= damageEvent.defender.getHealth()) {

			damageEvent.event.setCancelled(true);
			kill(damageEvent.attacker, damageEvent.defender, false);
		} else if(damageEvent.event.getFinalDamage() + damageEvent.executeUnder >= damageEvent.defender.getHealth()) {

			damageEvent.event.setCancelled(true);
			kill(damageEvent.attacker, damageEvent.defender, true);
		}
	}

	public static void kill(Player attacker, Player dead, boolean exeDeath) {

		Location spawnLoc = new Location(Bukkit.getWorld("world"), 20.5, 100, 0.5);

		dead.setHealth(dead.getMaxHealth());

		DecimalFormat df = new DecimalFormat("##0.00");
		AOutput.send(attacker, "&a&lKILL!&7 on &b" + dead.getName() + " &b+" + "5" + "XP" + " &6+" + "5" + df.format(5));
		AOutput.send(dead, "&cYou Died!");

		Non non = NonManager.getNon(dead);
		if(non == null) {
			dead.teleport(spawnLoc);
		} else {
			Misc.multiKill(attacker);
			non.respawn();
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
