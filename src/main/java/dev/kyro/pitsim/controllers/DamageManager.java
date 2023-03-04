package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.notdarkzone.Shield;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.CorruptedFeather;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.aitems.prot.ProtBoots;
import dev.kyro.pitsim.aitems.prot.ProtChestplate;
import dev.kyro.pitsim.aitems.prot.ProtHelmet;
import dev.kyro.pitsim.aitems.prot.ProtLeggings;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.overworld.Regularity;
import dev.kyro.pitsim.enchants.overworld.Telebow;
import dev.kyro.pitsim.enchants.tainted.effects.PurpleThumb;
import dev.kyro.pitsim.enchants.tainted.uncommon.ShieldBuster;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.ArmorReduction;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.BreadDealer;
import dev.kyro.pitsim.upgrades.DivineIntervention;
import dev.kyro.pitsim.upgrades.LifeInsurance;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class DamageManager implements Listener {
	public static List<LivingEntity> hitCooldownList = new ArrayList<>();
	public static List<LivingEntity> hopperCooldownList = new ArrayList<>();
	public static List<LivingEntity> nonHitCooldownList = new ArrayList<>();
	public static List<LivingEntity> bossHitCooldown = new ArrayList<>();

	public static Map<EntityShootBowEvent, Map<PitEnchant, Integer>> arrowMap = new HashMap<>();
	public static Map<Entity, LivingEntity> hitTransferMap = new HashMap<>();

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

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(event.getCause() != EntityDamageEvent.DamageCause.WITHER && event.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
			return;
		Player player = (Player) event.getEntity();
		if(event.getFinalDamage() >= player.getHealth()) {
			event.setCancelled(true);
			death(player);
		}
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player) || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.CUSTOM)
			return;
		Player player = (Player) event.getEntity();
		event.setCancelled(true);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.heal(event.getAmount());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBowShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	public void transferHit(LivingEntity attacker, Entity damager, LivingEntity defender, double damage) {
		if(attacker != damager) hitTransferMap.put(damager, attacker);
		defender.damage(damage, attacker);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity attacker = getAttacker(event.getDamager());
		LivingEntity defender = (LivingEntity) event.getEntity();

		if(defender.isDead()) return;

		Entity realDamager = event.getDamager();
		for(Map.Entry<Entity, LivingEntity> entry : DamageManager.hitTransferMap.entrySet()) {
			if(entry.getValue() != realDamager) continue;
			realDamager = entry.getKey();
			DamageManager.hitTransferMap.remove(entry.getKey());
			break;
		}

		if(PitSim.status.isDarkzone()) {
			PitMob attackerMob = DarkzoneManager.getPitMob(attacker);
			PitMob defenderMob = DarkzoneManager.getPitMob(defender);

			if(attackerMob != null && defenderMob != null) {
				event.setCancelled(true);
				return;
			}

			if(defenderMob != null) {
				try {
					event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
				} catch(Exception ignored) {}
			}

			for(SubLevel subLevel : DarkzoneManager.subLevels) {
				for(PitMob pitMob : subLevel.mobs) {
					for(LivingEntity entity : pitMob.getNameTag().getEntities()) {
						if(entity == defender) {
							event.setCancelled(true);
							transferHit(attacker, event.getDamager(), pitMob.getMob(), event.getDamage());
							return;
						}
						if(entity == attacker) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}

		if(defender instanceof ArmorStand) return;
		if(defender instanceof Slime && !(defender instanceof MagmaCube)) return;

		Map<PitEnchant, Integer> defenderEnchantMap = EnchantManager.getEnchantsOnPlayer(defender);
		boolean fakeHit = false;

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
//		Hit on non or by non
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(defender.getUniqueId()) &&
						!(realDamager instanceof Arrow)) {
			event.setCancelled(true);
			DamageManager.hitTransferMap.remove(event.getDamager());
			return;
		}
//		Regular player to player hit
		if(attackingNon == null && !Regularity.toReg.contains(defender.getUniqueId())) {
			fakeHit = hitCooldownList.contains(defender);
			if(hopperCooldownList.contains(defender) && HopperManager.isHopper(defender)) {
				event.setCancelled(true);
				return;
			}
		}

		if(bossHitCooldown.contains(defender)) {
			event.setCancelled(true);
			return;
		}

		if(Regularity.regCooldown.contains(defender.getUniqueId()) && !Regularity.toReg.contains(defender.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		if(!fakeHit) {
//			if(attackingNon == null) attacker.setHealth(Math.min(attacker.getHealth() + 1, attacker.getMaxHealth()));
			hitCooldownList.add(defender);
			hopperCooldownList.add(defender);
			nonHitCooldownList.add(defender);
			if(BossManager.isPitBoss(defender)) bossHitCooldown.add(defender);

			new BukkitRunnable() {
				int count = 0;

				@Override
				public void run() {
					if(++count == 15) cancel();

					if(count == 5) DamageManager.hitCooldownList.remove(defender);
					if(count == 10) DamageManager.hopperCooldownList.remove(defender);
					if(count == 15) DamageManager.nonHitCooldownList.remove(defender);
					if(count == 10) DamageManager.bossHitCooldown.remove(defender);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}

//		Reduce cpu load by not handling non v non
		if(attackingNon != null && defendingNon != null) {
			if(defender.getHealth() <= event.getFinalDamage()) {
				defender.setHealth(defender.getMaxHealth());
			} else {
				defender.setHealth(defender.getHealth() - event.getFinalDamage());
			}
			event.setDamage(0);
			return;
		}

		if(attackingNon != null) {
//			Non damage
			double damage = attackingNon.traits.contains(NonTrait.IRON_STREAKER) ? 9.6 : 7;
			if(Misc.isCritical(attacker)) damage *= 1.5;
			event.setDamage(damage);
		}

		AttackEvent.Pre preEvent;
		if(event.getEntity() instanceof Fireball) return;

		Map<PitEnchant, Integer> attackerEnchantMap = new HashMap<>();
		if(realDamager instanceof Slime && !(realDamager instanceof MagmaCube)) {
		} else if(realDamager instanceof Arrow) {
			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {
				if(!entry.getKey().getProjectile().equals(realDamager)) continue;
				attackerEnchantMap = arrowMap.get(entry.getKey());
				break;
			}
		} else if(realDamager instanceof Fireball) {
		} else if(realDamager instanceof LivingEntity) {
			attackerEnchantMap = EnchantManager.getEnchantsOnPlayer(attacker);
		}

//		Remove disabled enchants
		for(Map.Entry<PitEnchant, Integer> entry : new ArrayList<>(attackerEnchantMap.entrySet()))
			if(!entry.getKey().isEnabled()) attackerEnchantMap.remove(entry.getKey());
		for(Map.Entry<PitEnchant, Integer> entry : new ArrayList<>(defenderEnchantMap.entrySet()))
			if(!entry.getKey().isEnabled()) defenderEnchantMap.remove(entry.getKey());

		preEvent = new AttackEvent.Pre(event, realDamager, attackerEnchantMap, defenderEnchantMap, fakeHit);
		Bukkit.getServer().getPluginManager().callEvent(preEvent);
		if(preEvent.isCancelled()) {
			event.setCancelled(true);
			return;
		}

		AttackEvent.Apply applyEvent = new AttackEvent.Apply(preEvent);
		Bukkit.getServer().getPluginManager().callEvent(applyEvent);

		handleAttack(applyEvent);
		Bukkit.getServer().getPluginManager().callEvent(new AttackEvent.Post(applyEvent));
	}

	public static void handleAttack(AttackEvent.Apply attackEvent) {
//		AOutput.send(attackEvent.attacker, "Initial Damage: " + attackEvent.event.getDamage());

//		As strong as iron
		attackEvent.multipliers.add(ArmorReduction.getReductionMultiplier(attackEvent.getDefender()));

//		New player defence
		if(PitSim.status.isOverworld() && PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) && PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) &&
				attackEvent.getDefenderPlayer().getLocation().distance(MapManager.currentMap.getMid()) < 12) {
			if(attackEvent.getDefenderPitPlayer().prestige < 10) {
				int minutesPlayed = attackEvent.getDefenderPitPlayer().stats.minutesPlayed;
				double reduction = Math.max(50 - (minutesPlayed / 8.0), 0);
				attackEvent.multipliers.add(Misc.getReductionMultiplier(reduction));
				attackEvent.trueDamage *= Misc.getReductionMultiplier(reduction);
			}
		}

		double damage = attackEvent.getFinalDamage();
		if(PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) {
			Shield defenderShield = attackEvent.getDefenderPitPlayer().shield;
			double multiplier = 1;
			multiplier *= ShieldBuster.getMultiplier(attackEvent.getAttackerPlayer());
			if(PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer())) multiplier *= 2;
			if(defenderShield.isActive()) damage = defenderShield.damageShield(damage, multiplier);
		}
		attackEvent.getEvent().setDamage(damage);

		if(attackEvent.trueDamage != 0 || attackEvent.veryTrueDamage != 0) {
			double finalHealth = attackEvent.getDefender().getHealth() - attackEvent.trueDamage - attackEvent.veryTrueDamage;
			if(PurpleThumb.shouldPreventDeath(attackEvent.getDefenderPlayer())) finalHealth = Math.max(finalHealth, 1);
			if(finalHealth <= 0) {
				attackEvent.getEvent().setCancelled(true);
				kill(attackEvent, attackEvent.getAttacker(), attackEvent.getDefender(), KillType.KILL);
				return;
			} else {
				attackEvent.getDefender().setHealth(Math.max(finalHealth, 0));
			}
		}

		if(attackEvent.selfTrueDamage != 0 || attackEvent.selfVeryTrueDamage != 0) {
			double finalHealth = attackEvent.getAttacker().getHealth() - attackEvent.selfTrueDamage - attackEvent.selfVeryTrueDamage;
			if(PurpleThumb.shouldPreventDeath(attackEvent.getAttackerPlayer())) finalHealth = Math.max(finalHealth, 1);
			if(finalHealth <= 0) {
				attackEvent.getEvent().setCancelled(true);
				kill(attackEvent, attackEvent.getDefender(), attackEvent.getAttacker(), KillType.KILL);
				return;
			} else {
				attackEvent.getAttacker().setHealth(Math.max(finalHealth, 0));
//				attackEvent.attacker.damage(0);
			}
		}

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.getDefenderPlayer());
			pitPlayer.addDamage(attackEvent.getAttacker(), attackEvent.getEvent().getFinalDamage() + attackEvent.trueDamage);
		}

//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getDamage());
//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getFinalDamage());

		if(attackEvent.getEvent().getFinalDamage() + attackEvent.executeUnder >= attackEvent.getDefender().getHealth()) {
			if(PurpleThumb.shouldPreventDeath(attackEvent.getDefenderPlayer())) {
				attackEvent.getEvent().setDamage(0);
				attackEvent.getDefender().setHealth(1);
			} else {
				attackEvent.getEvent().setCancelled(true);
				boolean exeDeath = attackEvent.getEvent().getFinalDamage() < attackEvent.getDefender().getHealth();
				if(exeDeath) {
					kill(attackEvent, attackEvent.getAttacker(), attackEvent.getDefender(), KillType.KILL, KillModifier.EXECUTION);
				} else {
					kill(attackEvent, attackEvent.getAttacker(), attackEvent.getDefender(), KillType.KILL);
				}
			}
		}

		DamageIndicator.onAttack(attackEvent);
	}

	public static LivingEntity getAttacker(Entity damager) {
		if(damager instanceof Arrow) return (LivingEntity) ((Arrow) damager).getShooter();
		if(damager instanceof Fireball) return (LivingEntity) ((Fireball) damager).getShooter();
		if(damager instanceof Slime && !(damager instanceof MagmaCube)) return BlobManager.getOwner((Slime) damager);
		if(damager instanceof LivingEntity) return (LivingEntity) damager;
		return null;
	}

	public static void kill(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, KillType killType, KillModifier... killModifiers) {
		boolean killerIsPlayer = killer instanceof Player;
		boolean deadIsPlayer = dead instanceof Player;
		Player killerPlayer = killerIsPlayer ? (Player) killer : null;
		Player deadPlayer = deadIsPlayer ? (Player) dead : null;
		boolean killerIsRealPlayer = PlayerManager.isRealPlayer(killerPlayer);
		boolean deadIsRealPlayer = PlayerManager.isRealPlayer(deadPlayer);

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killerPlayer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(deadPlayer);
		Non killerNon = NonManager.getNon(killer);
		Non deadNon = NonManager.getNon(dead);
		PitMob killerMob = DarkzoneManager.getPitMob(killer);
		PitMob deadMob = DarkzoneManager.getPitMob(dead);

		KillEvent killEvent;

		if(deadIsRealPlayer && pitDead.megastreak instanceof RNGesus && RNGesus.isOnCooldown(pitDead)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					pitDead.megastreak.stop();
					pitDead.megastreak = new NoMegastreak(pitDead);
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}

		killEvent = new KillEvent(attackEvent, killer, dead, killType, killModifiers);
		Bukkit.getServer().getPluginManager().callEvent(killEvent);
		killEvent.damageItems();

		if(killerIsRealPlayer && deadIsPlayer) EnchantManager.incrementKillsOnJewels(killerPlayer);

		if(deadIsPlayer && killType != KillType.FAKE_KILL) {
			EntityPlayer nmsPlayer = ((CraftPlayer) dead).getHandle();
			nmsPlayer.setAbsorptionHearts(0);

			if(!LifeInsurance.isApplicable(deadPlayer) && !hasKillModifier(KillModifier.SELF_CHECKOUT, killModifiers))
				loseLives(dead, killer);

			if(deadIsRealPlayer) pitDead.endKillstreak();
			Telebow.teleShots.removeIf(teleShot -> teleShot.getShooter().equals(dead));
		}

		if(killType != KillType.FAKE_KILL) {
			dead.setHealth(dead.getMaxHealth());
			dead.playEffect(EntityEffect.HURT);
			Sounds.DEATH_FALL.play(dead);
			Sounds.DEATH_FALL.play(dead);
			Regularity.toReg.remove(dead.getUniqueId());
		}

		if(killerIsRealPlayer) {
			if(deadNon != null || deadIsRealPlayer) pitKiller.incrementKills();
			Misc.playKillSound(pitKiller);
		}

		if(PitSim.status.isOverworld()) {
			if(killerIsRealPlayer) {
				LevelManager.addXP(pitKiller.player, killEvent.getFinalXp());
				LevelManager.addGold(killEvent.getKillerPlayer(), (int) killEvent.getFinalGold());
			}
		} else {
			if(deadIsRealPlayer) {
				int finalSouls = killEvent.getFinalSouls();
				if(finalSouls != 0) {
					pitDead.taintedSouls -= finalSouls;
					DarkzoneManager.createSoulExplosion(killerPlayer, dead.getLocation(), finalSouls, finalSouls >= 50);
				}
			}
		}

		if(deadIsPlayer) {
			if(deadNon == null && dead.getWorld() != MapManager.getTutorial()) {
				Location spawnLoc = PitSim.getStatus() == PitSim.ServerStatus.DARKZONE ? MapManager.getDarkzoneSpawn() : MapManager.currentMap.getSpawn();

				if(killType != KillType.FAKE_KILL) dead.teleport(spawnLoc);
			} else if(deadNon != null) {
				deadNon.respawn(killType == KillType.FAKE_KILL);
			}
		} else {
			dead.remove();
		}

		if(killType != KillType.FAKE_KILL) {
			if(deadIsPlayer) {
				pitDead.bounty = 0;
				ChatTriggerManager.sendBountyInfo(pitDead);
			}
			for(PotionEffect potionEffect : dead.getActivePotionEffects()) {
				dead.removePotionEffect(potionEffect.getType());
			}
		}

		if(killerNon != null) {
			killerNon.rewardKill();
		}

		DecimalFormat df = new DecimalFormat("##0.##");
		String kill = null;
		if(deadMob != null) {
			kill = "&a&lKILL!&7 on " + deadMob.getDisplayName();
		} else if(killType != KillType.DEATH) {
			kill = PlaceholderAPI.setPlaceholders(killEvent.getDeadPlayer(), "&a&lKILL!&7 on %luckperms_prefix%" +
					(deadNon == null ? "%player_name%" : deadNon.displayName) + " &b+" + killEvent.getFinalXp() + "XP" +
					" &6+" + df.format(killEvent.getFinalGold()) + "g");
		}

		String death;
		String soulsLostString = "";
		if(PitSim.status.isDarkzone() && deadIsRealPlayer){
			int finalSouls = killEvent.getFinalSouls();
			if(finalSouls != 0) soulsLostString = " &f-" + finalSouls + " soul" + (finalSouls == 1 ? "" : "s");
		}
		if(killType == KillType.KILL && killerIsPlayer) {
			death = PlaceholderAPI.setPlaceholders(killEvent.getKillerPlayer(), "&c&lDEATH!&7 by %luckperms_prefix%" +
					(killerNon == null ? "%player_name%" : killerNon.displayName)) + soulsLostString;
		} else {
			death = "&c&lDEATH!" + soulsLostString;
		}

		String killActionBar = null;
		if(killType != KillType.DEATH && killerIsPlayer && deadMob == null) {
			killActionBar = "&7%luckperms_prefix%" + (deadNon == null ? "%player_name%" : deadNon.displayName) + " &a&lKILL!";
		}

		if(killerIsPlayer && !CitizensAPI.getNPCRegistry().isNPC(killer) && !pitKiller.killFeedDisabled && killType != KillType.DEATH) {
			AOutput.send(killEvent.getKiller(), PlaceholderAPI.setPlaceholders(killEvent.getDeadPlayer(), kill));
			pitKiller.stats.mobsKilled++; // TODO: this is definitely the wrong spot
		}
		if(deadIsPlayer && !pitDead.killFeedDisabled && killType != KillType.FAKE_KILL && killEvent != null)
			AOutput.send(killEvent.getDead(), death);
		String actionBarPlaceholder;
		if(killActionBar != null) {
			assert killEvent != null;
			actionBarPlaceholder = PlaceholderAPI.setPlaceholders(killEvent.getDeadPlayer(), killActionBar);
			KillEvent finalKillEvent = killEvent;
			new BukkitRunnable() {
				@Override
				public void run() {
					ActionBarManager.sendActionBar(finalKillEvent.getKillerPlayer(), actionBarPlaceholder);
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}

		if(killType == KillType.KILL && deadIsPlayer) {
			double finalDamage = 0;
			for(Map.Entry<UUID, Double> entry : pitDead.recentDamageMap.entrySet()) finalDamage += entry.getValue();
			for(Map.Entry<UUID, Double> entry : pitDead.recentDamageMap.entrySet()) {
				if(entry.getKey().equals(killEvent.getKiller().getUniqueId())) continue;

				Player assistPlayer = Bukkit.getPlayer(entry.getKey());
				if(assistPlayer == null) continue;
				double assistPercent = Math.max(Math.min(entry.getValue() / finalDamage, 1), 0);

				if(UpgradeManager.hasUpgrade(assistPlayer, "KILL_STEAL")) {
					int tier = UpgradeManager.getTier(assistPlayer, "KILL_STEAL");
					assistPercent += (tier * 10) / 100D;
					if(assistPercent >= 1) {
						Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(assistPlayer);
						Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
						EntityDamageByEntityEvent newEvent = new EntityDamageByEntityEvent(assistPlayer, dead, EntityDamageEvent.DamageCause.CUSTOM, 0);
						AttackEvent newAttackEvent = new AttackEvent(newEvent, attackerEnchant, defenderEnchant, false);

						DamageManager.fakeKill(newAttackEvent, assistPlayer, dead);
						continue;
					}
				}

				int assistXP = (int) Math.ceil(20 * assistPercent);
				double assistGold = 20 * assistPercent;

				PitPlayer assistPitPlayer = PitPlayer.getPitPlayer(assistPlayer);
				LevelManager.addXP(assistPitPlayer.player, assistXP);
				LevelManager.addGold(assistPlayer, (int) assistGold);

				Sounds.ASSIST.play(assistPlayer);
				String assist = "&a&lASSIST!&7 " + Math.round(assistPercent * 100) + "% on %luckperms_prefix%" +
						(deadNon == null ? "%player_name%" : deadNon.displayName) + " &b+" + assistXP + "XP" + " &6+" + df.format(assistGold) + "g";

				if(!assistPitPlayer.killFeedDisabled)
					AOutput.send(assistPlayer, PlaceholderAPI.setPlaceholders(killEvent.getDeadPlayer(), assist));
			}
		}

		if(deadIsPlayer) {
			pitDead.assistRemove.forEach(BukkitTask::cancel);
			pitDead.assistRemove.clear();
			pitDead.recentDamageMap.clear();

			String message = "%luckperms_prefix%";
			pitDead.prefix = PrestigeValues.getPlayerPrefixNameTag(pitDead.player) + PlaceholderAPI.setPlaceholders(pitDead.player, message);
		}
	}

	public static void loseLives(LivingEntity dead, LivingEntity killer) {
		if(!(dead instanceof Player)) return;
		if(MapManager.inDarkzone(dead.getLocation())) {
			//TODO: Handle ingredient loss in darkzone
//			Player deadPlayer = (Player) dead;
//
//			for(int i = 0; i < deadPlayer.getInventory().getSize(); i++) {
//				if(!corruptedFeather && BrewingIngredient.isIngredient(deadPlayer.getInventory().getItem(i))) {
//					ItemStack item = deadPlayer.getInventory().getItem(i);
//					BrewingIngredient ingredient = BrewingIngredient.getIngredientFromItemStack(item);
//					AOutput.send(deadPlayer, "&c- &8" + item.getAmount() + "x " + ingredient.color + item.getItemMeta().getDisplayName());
//					deadPlayer.getInventory().setItem(i, new ItemStack(Material.AIR));
//				}
//			}
//
//			return;
		}



		if(BoosterManager.getBooster("pvp").minutes <= 0) {
			Player deadPlayer = (Player) dead;
			PitPlayer pitDead = PitPlayer.getPitPlayer(deadPlayer);

			boolean divine = DivineIntervention.INSTANCE.attemptDivine(deadPlayer);
			boolean feather = false;
			boolean corruptedFeather = deadPlayer.getWorld().equals(MapManager.getDarkzone()) && ItemFactory.getItem(CorruptedFeather.class).useCorruptedFeather(killer, deadPlayer);
			if(!divine) feather = ItemFactory.getItem(FunkyFeather.class).useFeather(killer, deadPlayer);

			int livesLost = 0;

			for(int i = 0; i < deadPlayer.getInventory().getSize(); i++) {
				ItemStack itemStack = deadPlayer.getInventory().getItem(i);
				if(Misc.isAirOrNull(itemStack)) continue;
				NBTItem nbtItem = new NBTItem(itemStack);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(feather || divine || corruptedFeather) continue;
					MysticType mysticType = MysticType.getMysticType(itemStack);
					if(mysticType == null) continue;

					if(mysticType.isTainted() && !PitSim.status.isDarkzone()) continue;
					if(!mysticType.isTainted() && PitSim.status.isDarkzone()) continue;

					if(lives - 1 == 0) {
						deadPlayer.getInventory().setItem(i, new ItemStack(Material.AIR));
						deadPlayer.updateInventory();
						PlayerManager.sendItemBreakMessage(deadPlayer, itemStack);
						if(pitDead.stats != null) {
							pitDead.stats.itemsBroken++;
							LogManager.onItemBreak(deadPlayer, nbtItem.getItem());
						}
					} else {
						nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
						EnchantManager.setItemLore(nbtItem.getItem(), deadPlayer);
						deadPlayer.getInventory().setItem(i, nbtItem.getItem());
						livesLost++;
						LogManager.onItemLifeLost(deadPlayer, nbtItem.getItem());
					}
				}
			}

			if(!feather && !divine && !corruptedFeather) {
				deleteProt(deadPlayer);
				BreadDealer.handleBreadOnDeath(deadPlayer);
				deadPlayer.updateInventory();
			}
			if(!Misc.isAirOrNull(deadPlayer.getInventory().getLeggings())) {
				ItemStack pants = deadPlayer.getInventory().getLeggings();
				NBTItem nbtItem = new NBTItem(pants);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());

					MysticType mysticType = MysticType.getMysticType(pants);
					if(mysticType == null) return;

					if(mysticType.isTainted() && !PitSim.status.isDarkzone()) return;
					if(!mysticType.isTainted() && PitSim.status.isDarkzone()) return;

					if(!feather && !divine && !corruptedFeather) {
						if(lives - 1 == 0) {
							deadPlayer.getInventory().setLeggings(new ItemStack(Material.AIR));
							deadPlayer.updateInventory();
							PlayerManager.sendItemBreakMessage(deadPlayer, pants);
							if(pitDead.stats != null) {
								pitDead.stats.itemsBroken++;
								LogManager.onItemBreak(deadPlayer, nbtItem.getItem());
							}
						} else {
							nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
							EnchantManager.setItemLore(nbtItem.getItem(), deadPlayer);
							deadPlayer.getInventory().setLeggings(nbtItem.getItem());
							livesLost++;
							LogManager.onItemLifeLost(deadPlayer, nbtItem.getItem());
						}
					}
				}
			}

			if(pitDead.stats != null) pitDead.stats.livesLost += livesLost;
			PlayerManager.sendLivesLostMessage(deadPlayer, livesLost);
		}
	}

	public static boolean hasKillModifier(KillModifier killModifier, KillModifier... killModifiers) {
		return Arrays.asList(killModifiers).contains(killModifier);
	}

	public static void death(LivingEntity dead, KillModifier... killModifiers) {
		kill(null, null, dead, KillType.DEATH, killModifiers);
	}

	public static void fakeKill(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, KillModifier... killModifiers) {
		kill(attackEvent, killer, dead, KillType.FAKE_KILL, killModifiers);
	}

	public static void deleteProt(Player player) {
		PlayerInventory inventory = player.getInventory();

		if(ItemFactory.isThisItem(inventory.getHelmet(), ProtHelmet.class)) inventory.setHelmet(new ItemStack(Material.AIR));
		if(ItemFactory.isThisItem(inventory.getChestplate(), ProtChestplate.class)) inventory.setChestplate(new ItemStack(Material.AIR));
		if(ItemFactory.isThisItem(inventory.getLeggings(), ProtLeggings.class)) inventory.setLeggings(new ItemStack(Material.AIR));
		if(ItemFactory.isThisItem(inventory.getBoots(), ProtBoots.class)) inventory.setBoots(new ItemStack(Material.AIR));

		for(int i = 0; i < 36; i++) {
			ItemStack itemStack = inventory.getItem(i);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !pitItem.isProt) continue;
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
	}
}
