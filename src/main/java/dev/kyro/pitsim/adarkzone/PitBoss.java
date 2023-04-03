package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitBoss {

//	Player related
	private final Player summoner;
	private final Map<UUID, Double> damageMap = new HashMap<>();
	private final DropPool dropPool;
	private final PitBossBar bossBar = new PitBossBar(getDisplayName(), 1F);

//	Boss related
	private NPC npcBoss;
	private Player boss;
	private final BossTargetingSystem bossTargetingSystem = new BossTargetingSystem(this);
	private final PitEquipment equipment = DarkzoneManager.getDefaultEquipment();

//	Ability Related
	private final List<PitBossAbility> abilities = new ArrayList<>();
	private final Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>();
	private double skipRoutineChance = 0;
	private long lastRoutineExecuteTick = PitSim.currentTick;
	private int routineAbilityCooldownTicks = 20 * 8;

	private BukkitTask routineRunnable;
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
		for(PitBossAbility pitBossAbility : pitBossAbilities) if(pitBossAbility != null) abilities.add(pitBossAbility);
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
				.range(40)
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
		HashMap<UUID, Double> sortedDamageMap = getSortedDamageMap();
		List<Player> onlineDamagers = new ArrayList<>();
		for(Map.Entry<UUID, Double> entry : sortedDamageMap.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if(player != null) onlineDamagers.add(player);
		}

		for(Player damager : onlineDamagers) {
			AOutput.send(damager, "&4&m-------------------&4<&c&lBOSS SLAIN&4>&m-------------------");
			AOutput.send(damager, "&4&lTOP DAMAGE DEALT:");
			if(sortedDamageMap.size() >= 1) AOutput.send(damager, getDamageString(sortedDamageMap, getDamagerInPosition(sortedDamageMap, 0)));
			if(sortedDamageMap.size() >= 2) AOutput.send(damager, getDamageString(sortedDamageMap, getDamagerInPosition(sortedDamageMap, 1)));
			if(sortedDamageMap.size() >= 3) AOutput.send(damager, getDamageString(sortedDamageMap, getDamagerInPosition(sortedDamageMap, 2)));
			int playerPosition = getPositionOfDamager(sortedDamageMap, damager.getUniqueId());
			if(playerPosition > 3) {
				AOutput.send(damager, "");
				AOutput.send(damager, getDamageString(sortedDamageMap, damager.getUniqueId()));
			}
			AOutput.send(damager, "&4&m---------------------------------------------------");
		}

		dropPool.bossDistribution(sortedDamageMap, killer, this);

		double droppedSouls = getDroppedSouls();
		if(SoulBooster.INSTANCE.isActive()) droppedSouls *= 1 + (SoulBooster.getSoulsIncrease() / 100.0);
		DarkzoneManager.createSoulExplosion(sortedDamageMap,
				boss.getLocation().add(0, 0.5, 0), (int) droppedSouls, true);

		remove();
	}

	public UUID getDamagerInPosition(HashMap<UUID, Double> sortedDamageMap, int position) {
		if(sortedDamageMap.size() < position) return null;
		return sortedDamageMap.keySet().toArray(new UUID[0])[position];
	}

	public int getPositionOfDamager(HashMap<UUID, Double> sortedDamageMap, UUID damager) {
		int position = 0;
		for(UUID uuid : sortedDamageMap.keySet()) {
			if(uuid.equals(damager)) return position;
			position++;
		}
		return -1;
	}

	public String getDamageString(HashMap<UUID, Double> sortedDamageMap, UUID damager) {
		int position = getPositionOfDamager(sortedDamageMap, damager);

		Player player = Bukkit.getPlayer(damager);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(damager);
		String displayName = player != null ? Misc.getDisplayName(player) : offlinePlayer.getName();

		double damage = sortedDamageMap.get(damager);
		String positionColor;
		switch(position + 1) {
			case 1:
				positionColor = "&e";
				break;
			case 2:
				positionColor = "&f";
				break;
			case 3:
				positionColor = "&6";
				break;
			default:
				positionColor = "&7";
				break;
		}
		return "&4 * &7" + positionColor + (position + 1) + ". &7" + displayName + "&7 - &c" + Misc.getHearts(damage);
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

	public HashMap<UUID, Double> getSortedDamageMap() {
		LinkedHashMap<UUID, Double> sortedDamageMap = new LinkedHashMap<>();
		damageMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(entry -> sortedDamageMap.put(entry.getKey(), entry.getValue()));
		return sortedDamageMap;
	}

	public SubLevel getSubLevel() {
		return getSubLevelType().getSubLevel();
	}

	public Player getSummoner() {
		return summoner;
	}

	public Map<UUID, Double> getDamageMap() {
		return damageMap;
	}

	public DropPool getDropPool() {
		return dropPool;
	}

	public PitBossBar getBossBar() {
		return bossBar;
	}

	public NPC getNpcBoss() {
		return npcBoss;
	}

	public Player getBoss() {
		return boss;
	}

	public BossTargetingSystem getBossTargetingSystem() {
		return bossTargetingSystem;
	}

	public PitEquipment getEquipment() {
		return equipment;
	}

	public List<PitBossAbility> getAbilities() {
		return abilities;
	}

	public Map<PitBossAbility, Double> getRoutineAbilityMap() {
		return routineAbilityMap;
	}

	public BukkitTask getRoutineRunnable() {
		return routineRunnable;
	}

	public double getSkipRoutineChance() {
		return skipRoutineChance;
	}

	public void setSkipRoutineChance(double skipRoutineChance) {
		this.skipRoutineChance = skipRoutineChance;
	}

	public long getLastRoutineExecuteTick() {
		return lastRoutineExecuteTick;
	}

	public void setLastRoutineExecuteTick(long lastRoutineExecuteTick) {
		this.lastRoutineExecuteTick = lastRoutineExecuteTick;
	}

	public int getRoutineAbilityCooldownTicks() {
		return routineAbilityCooldownTicks;
	}

	public void setRoutineAbilityCooldownTicks(double routineAbilityCooldownTicks) {
		this.routineAbilityCooldownTicks = (int) Math.round(routineAbilityCooldownTicks);
	}
}
