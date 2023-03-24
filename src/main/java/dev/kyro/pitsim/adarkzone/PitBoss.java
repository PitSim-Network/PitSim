package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.boosters.SoulBooster;
import dev.kyro.pitsim.controllers.objects.PitBossBar;
import dev.kyro.pitsim.misc.MinecraftSkin;
import dev.kyro.pitsim.misc.Misc;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitBoss {

//	Player related
	public Player summoner;
	public Map<UUID, Double> damageMap = new HashMap<>();
	public DropPool dropPool;
	public PitBossBar bossBar = new PitBossBar(getDisplayName(), 1F);

//	Boss related
	public NPC npcBoss;
	public Player boss;
	public BossTargetingSystem bossTargetingSystem = new BossTargetingSystem(this);
	public PitEquipment equipment = DarkzoneManager.getDefaultEquipment();

//	Ability Related
	public List<PitBossAbility> abilities = new ArrayList<>();
	public Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>();
	public double skipRoutineChance = 0;
	public long lastRoutineExecuteTick = PitSim.currentTick;
	public int routineAbilityCooldownTicks = 20 * 8;

	public BukkitTask routineRunnable;
	private BukkitTask targetingRunnable;

	public PitBoss(Player summoner) {
		this.summoner = summoner;

		this.dropPool = createDropPool();
		BossManager.pitBosses.add(this);
		spawn();
	}

	public abstract SubLevelType getSubLevelType();
	public abstract String getRawDisplayName();
	public abstract ChatColor getChatColor();
	public abstract String getSkinName();
	public abstract double getMaxHealth();
	public abstract double getDamage();
	public abstract double getReach();
	public abstract double getReachRanged();
	public abstract int getSpeedLevel();
	public abstract int getDroppedSouls();
	public abstract DropPool createDropPool();

//	Internal events (override to add functionality)
	public void onSpawn() {}
	public void onDeath() {}

	public String getDisplayName() {
		return getChatColor() + getRawDisplayName();
	}

	public PitBoss abilities(PitBossAbility... pitBossAbilities) {
		abilities = Arrays.asList(pitBossAbilities);
		for(PitBossAbility ability : abilities) {
			ability.pitBoss(this);
			ability.onEnable();
			if(ability.getRoutineWeight() <= 0) continue;
			routineAbilityMap.put(ability, ability.getRoutineWeight());
		}
		return this;
	}

//	Where chance is a percent chance 0-100
	public PitBoss routineAbilitySkip(double chance) {
		skipRoutineChance = chance;
		return this;
	}

	public void delayNextRoutine(int ticks) {
		lastRoutineExecuteTick += ticks;
	}

	public PitBossAbility getRoutineAbility() {
		Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>(this.routineAbilityMap);
		for(Map.Entry<PitBossAbility, Double> entry : new ArrayList<>(routineAbilityMap.entrySet()))
			if(!entry.getKey().shouldExecuteRoutine()) routineAbilityMap.remove(entry.getKey());
		if(routineAbilityMap.isEmpty()) return null;
		return Misc.weightedRandom(routineAbilityMap);
	}

	public void spawn() {
		npcBoss = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, this.getDisplayName());
		npcBoss.setProtected(false);
		npcBoss.spawn(getSubLevel().getBossSpawnLocation());
		boss = (Player) npcBoss.getEntity();
		boss.setMaxHealth(getMaxHealth());
		boss.setHealth(getMaxHealth());
		equipment.setEquipment(npcBoss);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!npcBoss.isSpawned()) return;
				MinecraftSkin minecraftSkin = MinecraftSkin.getSkin(getSkinName());
				if(minecraftSkin != null) {
					SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
					npcBoss.addTrait(skinTrait);
					skinTrait.setSkinPersistent(getSkinName(), minecraftSkin.signature, minecraftSkin.skin);
				}

			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		CitizensNavigator navigator = (CitizensNavigator) npcBoss.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks(10)
				.stuckAction(null)
				.range(50)
				.attackRange(getReach());

		bossTargetingSystem.assignTarget();

		routineRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(routineAbilityMap.isEmpty()) return;
				if(lastRoutineExecuteTick + routineAbilityCooldownTicks > PitSim.currentTick) return;
				if(skipRoutineChance != 0 && Math.random() * 100 < skipRoutineChance) return;
				PitBossAbility routineAbility = getRoutineAbility();
				if(routineAbility == null) return;
				lastRoutineExecuteTick = PitSim.currentTick;
				routineAbility.onRoutineExecute();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);

		targetingRunnable = new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				Player boss = (Player) npcBoss.getEntity();
				if(boss != null && boss != PitBoss.this.boss) {
					PitBoss.this.boss = boss;
					boss.setMaxHealth(getMaxHealth());
					boss.setHealth(getMaxHealth());
				}

				if(bossTargetingSystem.target != null) Util.faceLocation(boss, bossTargetingSystem.target.getLocation());
				if(count % 5 == 0) bossTargetingSystem.assignTarget();

				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(getSpeedLevel() <= 0) return;
				Misc.applyPotionEffect((LivingEntity) npcBoss.getEntity(), PotionEffectType.SPEED, 99999, getSpeedLevel() - 1, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);

		onSpawn();
	}

	public void kill(Player killer) {
		dropPool.groupDistribution(killer, damageMap);

		double droppedSouls = getDroppedSouls();
		if(SoulBooster.INSTANCE.isActive()) droppedSouls *= 1 + (SoulBooster.getSoulsIncrease() / 100.0);
		DarkzoneManager.createSoulExplosion(null, boss.getLocation().add(0, 0.5, 0), (int) droppedSouls, true);

		remove();
	}

	public void remove() {
		for(PitBossAbility ability : abilities) ability.disable();
		npcBoss.destroy();
		bossBar.remove();
		if(routineRunnable!= null) routineRunnable.cancel();
		if(targetingRunnable != null) targetingRunnable.cancel();
		onDeath();
		getSubLevel().bossDeath();
		BossManager.pitBosses.remove(this);
	}

	public void onHealthChange() {
		bossBar.updateProgress((float) (boss.getHealth() / boss.getMaxHealth()));
	}

	public void alertDespawn() {
		for(Map.Entry<UUID, Double> entry : damageMap.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if(player == null) continue;
			Misc.sendTitle(player, "&c&lBOSS DESPAWNED!", 60);
			Misc.sendSubTitle(player, "&7No players nearby", 60);
		}
	}

	public SubLevel getSubLevel() {
		return getSubLevelType().getSubLevel();
	}
}
