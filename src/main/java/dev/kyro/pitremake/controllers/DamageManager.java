package dev.kyro.pitremake.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.enchants.Regularity;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;
import dev.kyro.pitremake.nons.Non;
import dev.kyro.pitremake.nons.NonManager;
import dev.kyro.pitremake.nons.NonTrait;
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
	public static List<Player> nonHitCooldownList = new ArrayList<>();
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
		Player attacker = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Arrow) event.getDamager()).getShooter();
		Player defender = (Player) event.getEntity();

		Non non = NonManager.getNon(attacker);
		/*
		Cancel if < 10 ticks and is a normal player attacking and < 12 ticks if non attacking. This is to give player hit priority
		over the non when attempting to attack because for some reason the nons have like 40 cps.
		 */
		if((non == null && hitCooldownList.contains(defender) && !Regularity.toReg.contains(attacker.getUniqueId())) ||
				(non != null && nonHitCooldownList.contains(defender))) {
			event.setCancelled(true);
			return;
		}
		DamageManager.hitCooldownList.add(defender);
		DamageManager.nonHitCooldownList.add(defender);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.hitCooldownList.remove(defender);
			}
		}.runTaskLater(PitRemake.INSTANCE, 10L);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.nonHitCooldownList.remove(defender);
			}
		}.runTaskLater(PitRemake.INSTANCE, 12L);
//		Vampire for nons
		if(non != null) {
			if(non.traits.contains(NonTrait.IRON_STREAKER)) {
				event.setDamage(10.5);
				attacker.setHealth(Math.min(attacker.getHealth() + 1, attacker.getMaxHealth()));
			} else {
				event.setDamage(7);
			}
		}

//		Applies enchants to an attack
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
//			Skip enchant application if the enchant is a bow enchant and is used in mele
			if(pitEnchant.applyType == ApplyType.BOWS && damageEvent.arrow == null) continue;
//			Skips enchant application if the enchant only works on mele hit and the event is from an arrow
			if(pitEnchant.meleOnly && damageEvent.arrow != null) continue;

			pitEnchant.onDamage(damageEvent);
		}

		double damage = damageEvent.getFinalDamage();

		damageEvent.event.setDamage(damage);

		AOutput.send(damageEvent.attacker, "Final Damage: " + damageEvent.event.getDamage());

		if(damageEvent.trueDamage != 0) {
			double finalHealth = damageEvent.defender.getHealth() - damageEvent.trueDamage;
			if(finalHealth <= 0) {
				kill(damageEvent.attacker, damageEvent.defender, false);
			} else {
				damageEvent.defender.setHealth(Math.max(finalHealth, 0));
			}
		}

		if(damageEvent.selfTrueDamage != 0) {
			double finalHealth = damageEvent.attacker.getHealth() - damageEvent.selfTrueDamage;
			if(finalHealth <= 0) {
				kill(damageEvent.attacker, damageEvent.defender, false);
			} else {
				damageEvent.attacker.setHealth(Math.max(finalHealth, 0));
				damageEvent.attacker.damage(0);
			}
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

		Non attackNon = NonManager.getNon(attacker);
		if(attackNon != null) {
			attackNon.rewardKill();
		}
	}
}
