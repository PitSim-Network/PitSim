package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
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
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player)) return;
		Player attacker = getAttacker(event.getDamager());
		Player defender = (Player) event.getEntity();

		Map<PitEnchant, Integer> defenderEnchantMap = EnchantManager.getEnchantsOnPlayer(defender);
		boolean fakeHit = false;

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
//		Hit on non or by non
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(defender.getUniqueId())) {
			event.setCancelled(true);
			return;
		}
//		Regular player to player hit
		if(attackingNon == null && !Regularity.toReg.contains(defender.getUniqueId())) {
			fakeHit = hitCooldownList.contains(defender);
		}

		if(!fakeHit) {
			if(attackingNon == null) attacker.setHealth(Math.min(attacker.getHealth() + 1, attacker.getMaxHealth()));
			hitCooldownList.add(defender);
			nonHitCooldownList.add(defender);
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if(++count == 15) cancel();

					if(count == 10) DamageManager.hitCooldownList.remove(defender);
					if(count == 15) DamageManager.nonHitCooldownList.remove(defender);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}

		AttackEvent.Pre preEvent = null;
		if(event.getDamager() instanceof Player) {

			preEvent = new AttackEvent.Pre(event, EnchantManager.getEnchantsOnPlayer(attacker), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof Arrow) {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				preEvent = new AttackEvent.Pre(event, arrowMap.get(entry.getKey()), defenderEnchantMap, fakeHit);
			}
		} else if(event.getDamager() instanceof Slime) {

			preEvent = new AttackEvent.Pre(event, new HashMap<>(), defenderEnchantMap, fakeHit);
		}
		if(preEvent == null) return;

		Bukkit.getServer().getPluginManager().callEvent(preEvent);
		if(preEvent.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		AttackEvent.Apply applyEvent = new AttackEvent.Apply(preEvent);
		Bukkit.getServer().getPluginManager().callEvent(applyEvent);
		if(applyEvent.fakeHit) {
			applyEvent.event.setDamage(0);
			return;
		}
		handleAttack(applyEvent);
		Bukkit.getServer().getPluginManager().callEvent(new AttackEvent.Post(applyEvent));
	}

	public static void handleAttack(AttackEvent.Apply attackEvent) {
//		AOutput.send(attackEvent.attacker, "Initial Damage: " + attackEvent.event.getDamage());

//		As strong as iron
		if(attackEvent.defender.getInventory().getLeggings() != null && attackEvent.defender.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS) {
			NBTItem pants = new NBTItem(attackEvent.defender.getInventory().getLeggings());
			if(pants.hasKey(NBTTag.ITEM_UUID.getRef())) {
				attackEvent.multiplier.add(0.86956521);
			}
		}

		double damage = attackEvent.getFinalDamage();
		attackEvent.event.setDamage(damage);

		if(attackEvent.trueDamage != 0) {
			double finalHealth = attackEvent.defender.getHealth() - attackEvent.trueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, false);
				return;
			} else {
				attackEvent.defender.setHealth(Math.max(finalHealth, 0));
			}
		}

		if(attackEvent.selfTrueDamage != 0 || attackEvent.selfVeryTrueDamage != 0) {
			double finalHealth = attackEvent.attacker.getHealth() - attackEvent.selfTrueDamage - attackEvent.selfVeryTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, false);
				return;
			} else {
				attackEvent.attacker.setHealth(Math.max(finalHealth, 0));
				attackEvent.attacker.damage(0);
			}
		}
//		}

//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getDamage());
//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getFinalDamage());

		if(attackEvent.event.getFinalDamage() >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, false);
		} else if(attackEvent.event.getFinalDamage() + attackEvent.executeUnder >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, true);
		}

		DamageIndicator.onAttack(attackEvent);
	}

	public static Player getAttacker(Entity damager) {

		if(damager instanceof Player) return (Player) damager;
		if(damager instanceof Arrow) return (Player) ((Arrow) damager).getShooter();
		if(damager instanceof Slime) return PitBlob.getOwner((Slime) damager);

		return null;
	}

	public static void kill(AttackEvent.Apply attackEvent, boolean exeDeath) {

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
		pitAttacker.incrementKills();

		Location spawnLoc = new Location(Bukkit.getWorld("pit"), -108.5, 86, 194.5, 45, 0);

		attackEvent.defender.setHealth(attackEvent.defender.getMaxHealth());
		attackEvent.defender.playEffect(EntityEffect.HURT);
		attackEvent.defender.playSound(attackEvent.defender.getLocation(), Sound.FALL_BIG, 1000, 1F);
		attackEvent.defender.playSound(attackEvent.defender.getLocation(), Sound.FALL_BIG, 1000, 1F);
		Regularity.toReg.remove(attackEvent.defender.getUniqueId());

		Non defendingNon = NonManager.getNon(attackEvent.defender);
		if(defendingNon == null) {
			attackEvent.defender.teleport(spawnLoc);
			Misc.multiKill(attackEvent.attacker);
		} else {
			Misc.multiKill(attackEvent.attacker);
			defendingNon.respawn();
		}

		pitDefender.megastreak.reset();
		for(PotionEffect potionEffect : attackEvent.defender.getActivePotionEffects()) {
			attackEvent.defender.removePotionEffect(potionEffect.getType());
		}

		Non attackNon = NonManager.getNon(attackEvent.attacker);
		if(attackNon != null) {
			attackNon.rewardKill();
		}

		KillEvent killEvent = new KillEvent(attackEvent, exeDeath);
		Bukkit.getServer().getPluginManager().callEvent(killEvent);

		PitSim.VAULT.depositPlayer(attackEvent.attacker, killEvent.getFinalGold());

		DecimalFormat df = new DecimalFormat("##0.00");
		String kill = "&a&lKILL!&7 on %luckperms_prefix%%player_name% &b+" +
				killEvent.getFinalXp() + "XP" +" &6+" + df.format(killEvent.getFinalGold()) + "g";
		String death = "&c&lDEATH! &7by %luckperms_prefix%%player_name%";
		String killActionBar = "&7%luckperms_prefix%%player_name% &a&lKILL!";
		AOutput.send(attackEvent.attacker, PlaceholderAPI.setPlaceholders(attackEvent.defender, kill));
		AOutput.send(attackEvent.defender, PlaceholderAPI.setPlaceholders(attackEvent.attacker, death));
		String actionBarPlaceholder = PlaceholderAPI.setPlaceholders(attackEvent.defender, killActionBar);
		new BukkitRunnable() {
			@Override
			public void run() {

				Misc.sendActionBar(attackEvent.attacker, actionBarPlaceholder);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}
}
