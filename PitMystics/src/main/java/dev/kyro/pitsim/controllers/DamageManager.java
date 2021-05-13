package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
import dev.kyro.pitsim.nons.NonTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
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
	public static List<Player> fakeHitCooldownList = new ArrayList<>();
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
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
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

		if(!(event.getEntity() instanceof Player) ||
				(!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow) && !(event.getDamager() instanceof Slime))) return;
		Player attacker = null;
		if(event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if(event.getDamager() instanceof Arrow) {
			attacker = (Player) ((Arrow) event.getDamager()).getShooter();
		} else if(event.getDamager() instanceof Slime) {

			attacker = PitBlob.getOwner((Slime) event.getDamager());
			if(attacker == null) return;
		}
		assert attacker != null;
		Player defender = (Player) event.getEntity();

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
		boolean fakeHit = false;

//		Hit on non or by non
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(attacker.getUniqueId())) {
			event.setCancelled(true);
			return;
		}
//		Regular player to player hit
		if(attackingNon == null && !Regularity.toReg.contains(attacker.getUniqueId())) {
			fakeHit = fakeHitCooldownList.contains(defender);
		}

		Bukkit.broadcastMessage(fakeHit + "");
		hitCooldownList.add(defender);
		fakeHitCooldownList.add(defender);
		nonHitCooldownList.add(defender);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.hitCooldownList.remove(defender);
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.fakeHitCooldownList.remove(defender);
			}
		}.runTaskLater(PitSim.INSTANCE, 6L);
		new BukkitRunnable() {
			@Override
			public void run() {
				DamageManager.nonHitCooldownList.remove(defender);
			}
		}.runTaskLater(PitSim.INSTANCE, 12L);
//		Vampire for nons
		if(!fakeHit) {
			int healing = 0;
			if(event.getDamager() instanceof Player) healing = 1;
			else if(event.getDamager() instanceof Arrow) {
				if(((Arrow) event.getDamager()).isCritical()) healing = 3;
			}
			attacker.setHealth(Math.min(attacker.getHealth() + healing, attacker.getMaxHealth()));
		}
		if(attackingNon != null) {
			if(attackingNon.traits.contains(NonTrait.IRON_STREAKER)) {
				event.setDamage(10.5);
			} else {
				event.setDamage(7);
			}
		}

//		Applies enchants to an attack
		if(event.getDamager() instanceof Player) {

			handleAttack(new DamageEvent(event, EnchantManager.getEnchantsOnPlayer((Player) event.getDamager()), fakeHit));
		} else if(event.getDamager() instanceof Arrow) {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				handleAttack(new DamageEvent(event, arrowMap.get(entry.getKey()), fakeHit));
			}
		} else if(event.getDamager() instanceof Slime) {

			handleAttack(new DamageEvent(event, EnchantManager.getEnchantsOnPlayer(attacker), fakeHit));
		}
	}

	public static void handleAttack(DamageEvent damageEvent) {

//		AOutput.send(damageEvent.attacker, "Initial Damage: " + damageEvent.event.getDamage());

		if(damageEvent.slime == null) {
			for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
//				Skip if fake hit and enchant doesn't handle fake hits
				if(!pitEnchant.fakeHits && damageEvent.fakeHit) continue;
//				Skip enchant application if the enchant is a bow enchant and is used in mele
				if(pitEnchant.applyType == ApplyType.BOWS && damageEvent.arrow == null) continue;
//				Skips enchant application if the enchant only works on mele hit and the event is from an arrow
				if(pitEnchant.meleOnly && damageEvent.arrow != null) continue;

				pitEnchant.onDamage(damageEvent);
			}

			double damage = damageEvent.getFinalDamage();

			damageEvent.event.setDamage(damage);

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
		}

//		AOutput.send(damageEvent.attacker, "Final Damage: " + damageEvent.event.getDamage());

		if(damageEvent.event.getFinalDamage() >= damageEvent.defender.getHealth()) {

			damageEvent.event.setCancelled(true);
			kill(damageEvent.attacker, damageEvent.defender, false);
		} else if(damageEvent.event.getFinalDamage() + damageEvent.executeUnder >= damageEvent.defender.getHealth()) {

			damageEvent.event.setCancelled(true);
			kill(damageEvent.attacker, damageEvent.defender, true);
		}
	}

	public static void kill(Player attacker, Player dead, boolean exeDeath) {

		Location spawnLoc = new Location(Bukkit.getWorld("pit"), -107.5, 111, 193.5, 45, 0);

		dead.setHealth(dead.getMaxHealth());

		DecimalFormat df = new DecimalFormat("##0.00");
		AOutput.send(attacker, "&a&lKILL!&7 on &b" + dead.getName() + " &b+" + "5" + "XP" + " &6+" + df.format(5) + "g");
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
