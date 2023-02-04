package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.misc.MinecraftSkin;
import dev.kyro.pitsim.misc.Misc;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitBoss {

//	Player related
	public Player summoner;
	public Map<UUID, Double> damageMap = new HashMap<>();
	public DropPool dropPool;

//	Boss related
	public NPC npcBoss;
	public Player boss;
	public BossTargetingSystem bossTargetingSystem;
	public PitEquipment equipment = DarkzoneManager.getDefaultEquipment();

//	Ability Related
	public List<PitBossAbility> abilities = new ArrayList<>();
	public Map<RoutinePitBossAbility, Double> routineAbilityMap = new HashMap<>();
	public double skipRoutineChance = 0;
	public long lastRoutineExecuteTick;
	public int routineAbilityCooldownTicks = 20 * 5;

	public BukkitTask routineRunnable;
	private BukkitTask targetingRunnbale;

	public PitBoss(Player summoner) {
		this.summoner = summoner;
		this.bossTargetingSystem = new BossTargetingSystem(this);

		this.dropPool = createDropPool();
		BossManager.pitBosses.add(this);
		spawn();
	}

	public abstract SubLevelType getSubLevelType();
	public abstract String getRawDisplayName();
	public abstract ChatColor getChatColor();
	public abstract String getSkinName();
	public abstract int getMaxHealth();
	public abstract double getMeleeDamage();
	public abstract double getReach();
	public abstract double getReachRanged();
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
			if(!(ability instanceof RoutinePitBossAbility)) continue;
			RoutinePitBossAbility routineAbility = (RoutinePitBossAbility) ability;
			routineAbilityMap.put(routineAbility, routineAbility.getRoutineWeight());
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

		bossTargetingSystem.pickTarget();

		routineRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(routineAbilityMap.isEmpty()) return;
				if(lastRoutineExecuteTick + routineAbilityCooldownTicks > PitSim.currentTick) return;
				if(skipRoutineChance != 0 && Math.random() * 100 < skipRoutineChance) return;
				lastRoutineExecuteTick = PitSim.currentTick;

				PitBossAbility routineAbility = getRoutineAbility();
				routineAbility.onRoutineExecute();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);

		targetingRunnbale = new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				Player boss = (Player) npcBoss.getEntity();
				if(boss != null) PitBoss.this.boss = boss;
//				EntityTarget target = npcBoss.getNavigator().getEntityTarget();
//				if(target != null) Util.faceLocation(boss, target.getTarget().getLocation());

				if(count % 5 == 0) bossTargetingSystem.pickTarget();
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1);
	}

	public void kill(Player killer) {
		dropPool.groupDistribution(killer, damageMap);
		remove();
	}

	public void remove() {
		for(PitBossAbility ability : abilities) ability.disable();
		npcBoss.destroy();
		if(routineRunnable!= null) routineRunnable.cancel();
		if(targetingRunnbale!= null) targetingRunnbale.cancel();
		onDeath();
		getSubLevel().bossDeath();
		BossManager.pitBosses.remove(this);
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
