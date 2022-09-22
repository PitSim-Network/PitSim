package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enchants.Telebow;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.NonTrait;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.*;
import dev.kyro.pitsim.misc.tainted.CorruptedFeather;
import dev.kyro.pitsim.perks.AssistantToTheStreaker;
import dev.kyro.pitsim.upgrades.DivineIntervention;
import dev.kyro.pitsim.upgrades.LifeInsurance;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.*;
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
	public void onWitherDamage(EntityDamageEvent event) {
		if(event.getCause() != EntityDamageEvent.DamageCause.WITHER || !(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if(event.getFinalDamage() >= player.getHealth()) death(player);
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity attacker = getAttacker(event.getDamager());
		LivingEntity defender = (LivingEntity) event.getEntity();

		Map<PitEnchant, Integer> defenderEnchantMap = EnchantManager.getEnchantsOnPlayer(defender);
		boolean fakeHit = false;

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
//		Hit on non or by non
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(defender.getUniqueId()) &&
						!(event.getDamager() instanceof Arrow)) {
			event.setCancelled(true);
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

		if( bossHitCooldown.contains(defender)) {
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

			if(defender instanceof Player) {
				boolean isBoss = (PitBoss.isPitBoss((Player) defender));
				if(isBoss) bossHitCooldown.add(defender);
			}

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

		AttackEvent.Pre preEvent = null;
		if(event.getEntity() instanceof Fireball) return;
		if(event.getDamager() instanceof Slime && !(event.getDamager() instanceof MagmaCube)) {
			preEvent = new AttackEvent.Pre(event, new HashMap<>(), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof Arrow) {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {
				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				preEvent = new AttackEvent.Pre(event, arrowMap.get(entry.getKey()), defenderEnchantMap, fakeHit);
			}
		} else if(event.getDamager() instanceof Fireball) {
			preEvent = new AttackEvent.Pre(event, new HashMap<>(), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof LivingEntity) {
			preEvent = new AttackEvent.Pre(event, EnchantManager.getEnchantsOnPlayer(attacker), defenderEnchantMap, fakeHit);
		}
		if(preEvent == null) return;

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
		attackEvent.multipliers.add(ArmorReduction.getReductionMultiplier(attackEvent.defender));

		double damage = attackEvent.getFinalDamage();
		attackEvent.event.setDamage(damage);

		if(attackEvent.trueDamage != 0 || attackEvent.veryTrueDamage != 0) {
			double finalHealth = attackEvent.defender.getHealth() - attackEvent.trueDamage - attackEvent.veryTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, attackEvent.attacker, attackEvent.defender, false, KillType.DEFAULT);
				return;
			} else {
				attackEvent.defender.setHealth(Math.max(finalHealth, 0));
			}
		}

		if(attackEvent.selfTrueDamage != 0 || attackEvent.selfVeryTrueDamage != 0) {
			double finalHealth = attackEvent.attacker.getHealth() - attackEvent.selfTrueDamage - attackEvent.selfVeryTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, attackEvent.defender, attackEvent.attacker, false, KillType.DEFAULT);
				return;
			} else {
				attackEvent.attacker.setHealth(Math.max(finalHealth, 0));
//				attackEvent.attacker.damage(0);
			}
		}

		if(attackEvent.defenderIsPlayer) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defenderPlayer);
			pitPlayer.addDamage(attackEvent.attacker, attackEvent.event.getFinalDamage() + attackEvent.trueDamage);
		}

//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getDamage());
//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getFinalDamage());

		if(attackEvent.event.getFinalDamage() >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, attackEvent.attacker, attackEvent.defender, false, KillType.DEFAULT);
		} else if(attackEvent.event.getFinalDamage() + attackEvent.executeUnder >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, attackEvent.attacker, attackEvent.defender, true, KillType.DEFAULT);
		}

		DamageIndicator.onAttack(attackEvent);
	}

	public static LivingEntity getAttacker(Entity damager) {

		if(damager instanceof Arrow) return (LivingEntity) ((Arrow) damager).getShooter();
		if(damager instanceof Fireball) return (LivingEntity) ((Fireball) damager).getShooter();
		if(damager instanceof Slime && !(damager instanceof MagmaCube)) return PitBlob.getOwner((Slime) damager);
		if(damager instanceof LivingEntity) return (LivingEntity) damager;

		return null;
	}

	public static void kill(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, boolean exeDeath, KillType killType) {
		boolean killerIsPlayer = killer instanceof Player;
		boolean deadIsPlayer = dead instanceof Player;
		Player killerPlayer = killerIsPlayer ? (Player) killer : null;
		Player deadPlayer = deadIsPlayer ? (Player) dead : null;

		KillEvent killEvent = null;
		OofEvent oofEvent = null;

		if(killType == KillType.DEATH) {
			oofEvent = new OofEvent(deadPlayer);
			Bukkit.getPluginManager().callEvent(oofEvent);
		}
		else {
			killEvent = new KillEvent(attackEvent, killer, dead, exeDeath);
			Bukkit.getServer().getPluginManager().callEvent(killEvent);
		}

		if(killerIsPlayer && deadIsPlayer) {
			EnchantManager.incrementKills(killerPlayer, deadPlayer);
		}

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killerPlayer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(deadPlayer);

		if(deadIsPlayer && killType != KillType.FAKE) {
			EntityPlayer nmsPlayer = ((CraftPlayer) dead).getHandle();
			nmsPlayer.setAbsorptionHearts(0);

			if(EnchantManager.getEnchantLevel(deadPlayer, EnchantManager.getEnchant("sco")) == 0) {
				if(!LifeInsurance.isApplicable(deadPlayer)) loseLives(dead, killer);
			}

			if(NonManager.getNon(dead) == null) pitDead.endKillstreak();
			Telebow.teleShots.removeIf(teleShot -> teleShot.getShooter().equals(dead));
		}

		if(killType != KillType.FAKE) {
			dead.setHealth(dead.getMaxHealth());
			dead.playEffect(EntityEffect.HURT);
			Sounds.DEATH_FALL.play(dead);
			Sounds.DEATH_FALL.play(dead);
			Regularity.toReg.remove(dead.getUniqueId());
		}

		Non killingNon = NonManager.getNon(killer);
		if(killerIsPlayer) {
			if(killingNon == null) {
				Non deadNon = NonManager.getNon(dead);
				if(deadNon != null || Bukkit.getOnlinePlayers().contains(deadPlayer)) {
					pitKiller.incrementKills();
				}
			}

			Misc.multiKill(killerPlayer);
		}

		Non deadNon = NonManager.getNon(dead);
		if(deadIsPlayer) {
			if(deadNon == null && dead.getWorld() != MapManager.getTutorial()) {
				Location spawnLoc = MapManager.currentMap.getSpawn(dead.getWorld());
				if(killType != KillType.FAKE)dead.teleport(spawnLoc);
			} else if(deadNon != null) {
				deadNon.respawn();
			}
		} else {
			dead.remove();
		}

		if(killType != KillType.FAKE) {
			if(deadIsPlayer) {
				pitDead.bounty = 0;
			}
			for(PotionEffect potionEffect : dead.getActivePotionEffects()) {
				dead.removePotionEffect(potionEffect.getType());
			}
		}

		if(killingNon != null) {
			killingNon.rewardKill();
		}

		if(killerIsPlayer && killEvent != null) {
			LevelManager.addXP(pitKiller.player, killEvent.getFinalXp());
			LevelManager.addGold(killEvent.killerPlayer, (int) killEvent.getFinalGold());
		}

		DecimalFormat df = new DecimalFormat("##0.00");
		String kill = null;
		if(!deadIsPlayer && PitMob.isPitMob(dead)) kill = ChatColor.translateAlternateColorCodes('&', "&a&lKILL!&7 on " + PitMob.getPitMob(dead).displayName);
		else if(killType != KillType.DEATH) kill = PlaceholderAPI.setPlaceholders(killEvent.deadPlayer, "&a&lKILL!&7 on %luckperms_prefix%" + (deadNon == null ? "%player_name%" : deadNon.displayName)
				+ " &b+" + killEvent.getFinalXp() + "XP" + " &6+" + df.format(killEvent.getFinalGold()) + "g");
		String death;
		if(!killerIsPlayer) death = ChatColor.translateAlternateColorCodes('&', "&c&lDEATH!");
		else if(killType == KillType.DEFAULT) death = PlaceholderAPI.setPlaceholders(killEvent.killerPlayer, "&c&lDEATH! &7by %luckperms_prefix%" + (killingNon == null ? "%player_name%" : killingNon.displayName));
		else death = "&c&lDEATH!";
		String killActionBar = null;
		if(killerIsPlayer) killActionBar = "&7%luckperms_prefix%" + (deadNon == null ? "%player_name%" : deadNon.displayName) + " &a&lKILL!";
		else if(PitMob.isPitMob(dead)) killActionBar = PitMob.getPitMob(dead).displayName + " &a&lKILL!";

			if(killerIsPlayer && !CitizensAPI.getNPCRegistry().isNPC(killer) && !pitKiller.killFeedDisabled && killType != KillType.DEATH) {
				AOutput.send(killEvent.killer, PlaceholderAPI.setPlaceholders(killEvent.deadPlayer, kill));
					pitKiller.stats.mobsKilled++;
			}
			if(deadIsPlayer && !pitDead.killFeedDisabled && killType != KillType.FAKE && killEvent != null)
				AOutput.send(killEvent.dead, death);
			String actionBarPlaceholder;
			if(killType != KillType.DEATH && killerIsPlayer) {
				actionBarPlaceholder = PlaceholderAPI.setPlaceholders(killEvent.deadPlayer, killActionBar);
				KillEvent finalKillEvent = killEvent;
				new BukkitRunnable() {
					@Override
					public void run() {
						Misc.sendActionBar(finalKillEvent.killerPlayer, actionBarPlaceholder);
					}
				}.runTaskLater(PitSim.INSTANCE, 1L);
			}


		if(killType != KillType.FAKE) {
			if(killType != KillType.DEATH && deadIsPlayer) {


			double finalDamage = 0;
			for(Map.Entry<UUID, Double> entry : pitDead.recentDamageMap.entrySet()) {

				finalDamage += entry.getValue();
			}

			for(Map.Entry<UUID, Double> entry : pitDead.recentDamageMap.entrySet()) {

				if(entry.getKey().equals(killEvent.killer.getUniqueId())) continue;

				Player assistPlayer = Bukkit.getPlayer(entry.getKey());
				if(assistPlayer == null) continue;
//			TODO: Fix assist erroring (its rare so not super important)
				double assistPercent = Math.max(Math.min(entry.getValue() / finalDamage, 1), 0);

				if(UpgradeManager.hasUpgrade(assistPlayer, "KILL_STEAL")) {
					int tier = UpgradeManager.getTier(assistPlayer, "KILL_STEAL");
					assistPercent += (tier * 10) / 100D;
					if(assistPercent >= 1) {
						Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(assistPlayer);
						Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
						EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(assistPlayer, dead, EntityDamageEvent.DamageCause.CUSTOM, 0);
						AttackEvent aEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

						DamageManager.fakeKill(aEvent, assistPlayer, dead, false);
						continue;
					}
				}

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(assistPlayer);
				if(pitPlayer.hasPerk(AssistantToTheStreaker.INSTANCE)) {
					pitPlayer.incrementAssist(assistPercent);
				}

				int xp = (int) Math.ceil(20 * assistPercent);
				double gold = 20 * assistPercent;

				PitPlayer assistPitPlayer = PitPlayer.getPitPlayer(assistPlayer);
				LevelManager.addXP(assistPitPlayer.player, xp);
//			OldLevelManager.incrementLevel(assistPlayer);

				if(killEvent.getFinalGold() > 10) {
					LevelManager.addGold(assistPlayer, 10);
				} else {
					LevelManager.addGold(assistPlayer, (int) gold);
				}

				Sounds.ASSIST.play(assistPlayer);
				String assist = "&a&lASSIST!&7 " + Math.round(assistPercent * 100) + "% on %luckperms_prefix%" +
						(deadNon == null ? "%player_name%" : deadNon.displayName) + " &b+" + xp + "XP" + " &6+" + df.format(gold) + "g";

				if(!assistPitPlayer.killFeedDisabled)
					AOutput.send(assistPlayer, PlaceholderAPI.setPlaceholders(killEvent.deadPlayer, assist));
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
	}

	public static void loseLives(LivingEntity dead, LivingEntity killer) {
		if(!(dead instanceof Player)) return;
		if(MapManager.inDarkzone(dead.getLocation())) {
			Player deadPlayer = (Player) dead;
			boolean corrupted_feather = CorruptedFeather.useCorruptedFeather(killer, deadPlayer);
			for(int i = 0; i < deadPlayer.getInventory().getSize(); i++) {
				if(!corrupted_feather && BrewingIngredient.isIngredient(deadPlayer.getInventory().getItem(i))) {
					ItemStack item = deadPlayer.getInventory().getItem(i);
					BrewingIngredient ingredient = BrewingIngredient.getIngredientFromItemStack(item);
					AOutput.send(deadPlayer, "&c- &8" + item.getAmount() + "x " + ingredient.color + item.getItemMeta().getDisplayName());
					deadPlayer.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
			}

			return;
		}

		if(BoosterManager.getBooster("pvp").minutes <= 0) {
			Player deadPlayer = (Player) dead;
			PitPlayer pitDead = PitPlayer.getPitPlayer(deadPlayer);


			boolean divine = DivineIntervention.INSTANCE.isDivine(deadPlayer);
			boolean feather = FunkyFeather.useFeather(killer, deadPlayer, divine);


			for(int i = 0; i < deadPlayer.getInventory().getSize(); i++) {
				ItemStack itemStack = deadPlayer.getInventory().getItem(i);
				if(Misc.isAirOrNull(itemStack)) continue;
				NBTItem nbtItem = new NBTItem(itemStack);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(feather || divine) return;
					if(lives - 1 == 0) {
						deadPlayer.getInventory().remove(itemStack);

						if(pitDead.stats != null) pitDead.stats.itemsBroken++;
					} else {
						nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
						EnchantManager.setItemLore(nbtItem.getItem(), deadPlayer);
						deadPlayer.getInventory().setItem(i, nbtItem.getItem());

						if(pitDead.stats != null) pitDead.stats.livesLost++;
					}
				}
			}


			if(!feather && !divine) {
				ProtArmor.deleteArmor(deadPlayer);
				YummyBread.deleteBread(deadPlayer);
			}
			if(!Misc.isAirOrNull(deadPlayer.getInventory().getLeggings())) {
				ItemStack pants = deadPlayer.getInventory().getLeggings();
				NBTItem nbtItem = new NBTItem(pants);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(!feather && !divine) {
						if(lives - 1 == 0) {
							deadPlayer.getInventory().setLeggings(new ItemStack(Material.AIR));

							if(pitDead.stats != null) pitDead.stats.itemsBroken++;
						} else {
							nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
							EnchantManager.setItemLore(nbtItem.getItem(), deadPlayer);
							deadPlayer.getInventory().setLeggings(nbtItem.getItem());

							if(pitDead.stats != null) pitDead.stats.livesLost++;
						}
					}
				}
			}
		}
	}


	public static void death(LivingEntity dead) {
		kill(null, null, dead, false, KillType.DEATH);
	}

	public static void fakeKill(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, boolean exeDeath) {
		kill(attackEvent, killer, dead, exeDeath, KillType.FAKE);
	}

	public static boolean isNaked(LivingEntity entity) {
		if(!Misc.isAirOrNull(entity.getEquipment().getHelmet())) return false;
		if(!Misc.isAirOrNull(entity.getEquipment().getChestplate())) return false;
		if(!Misc.isAirOrNull(entity.getEquipment().getLeggings())) return false;
		return Misc.isAirOrNull(entity.getEquipment().getBoots());
	}
}
