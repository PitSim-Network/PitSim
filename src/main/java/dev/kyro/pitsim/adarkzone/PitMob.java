package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.SoulBranch;
import dev.kyro.pitsim.boosters.SoulBooster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.uncommon.Reaper;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PitMob implements Listener {
	private Creature mob;
	private DropPool dropPool;
	private PitNameTag nameTag;
	private final MobStatus mobStatus;

	public PitMob(Location spawnLocation, MobStatus mobStatus) {
		this.mobStatus = mobStatus;
		if(spawnLocation == null) return;
		this.dropPool = createDropPool();
		spawn(spawnLocation);
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract SubLevelType getSubLevelType();
	public abstract Creature createMob(Location spawnLocation);
	public abstract String getRawDisplayName();
	public abstract ChatColor getChatColor();
	public abstract int getMaxHealth();
	public abstract double getDamage();
	public abstract int getSpeedAmplifier();
	public abstract int getDroppedSouls();
	public abstract DropPool createDropPool();
	public abstract PitNameTag createNameTag();

	//	Internal events (override to add functionality)
	public void onSpawn() {}
	public void onDeath() {}

	public String getRawDisplayNamePlural() {
		return getRawDisplayName() + "s";
	}

	public String getDisplayName() {
		return getChatColor() + getRawDisplayName();
	}

	public String getDisplayNamePlural() {
		return getChatColor() + getRawDisplayNamePlural();
	}

	public void spawn(Location spawnLocation) {
		mob = createMob(spawnLocation);
		if(mob.isInsideVehicle()) mob.getVehicle().remove();
		mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, getSpeedAmplifier(), true, false));
		mob.setMaxHealth(getMaxHealth());
		mob.setHealth(getMaxHealth());

		nameTag = createNameTag();
		nameTag.attach();

		onSpawn();
	}

	public void kill(PitPlayer pitKiller) {
		Player killer = pitKiller == null ? null : pitKiller.player;
		if(mobStatus == MobStatus.STANDARD) {
			dropPool.singleDistribution(killer);

			double soulChance = 0.05;
			soulChance *= 1 + (Reaper.getSoulChanceIncrease(killer) / 100.0);
			soulChance *= 1 + (ProgressionManager.getUnlockedEffectAsValue(
					pitKiller, SoulBranch.INSTANCE, SkillBranch.PathPosition.FIRST_PATH, "soul-chance-mobs") / 100.0);
			if(Math.random() < soulChance) {
				double droppedSouls = getDroppedSouls();
				if(SoulBooster.INSTANCE.isActive()) droppedSouls *= 1 + (SoulBooster.getSoulsIncrease() / 100.0);
				DarkzoneManager.createSoulExplosion(killer,
						getMob().getLocation().add(0, 0.5, 0), (int) droppedSouls, false);
			}
		}
		remove();
	}

	public void remove() {
		if(mob != null) mob.remove();
		nameTag.remove();
		getSubLevel().mobTargetingSystem.changeTargetCooldown.remove(this);
		HandlerList.unregisterAll(this);
		onDeath();
		getSubLevel().mobs.remove(this);
	}

	public void setTarget(Player target) {
		if(target == getTarget()) return;
		mob.setTarget(target);
	}

	public Player getTarget() {
		LivingEntity target = mob.getTarget();
		if(!(target instanceof Player)) mob.setTarget(null);
		return (Player) mob.getTarget();
	}

	public Creature getMob() {
		return mob;
	}

	public void setMob(Creature mob) {
		this.mob = mob;
	}

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) {
			for(PitMob pitMob : subLevel.mobs) if(pitMob == this) return subLevel;
		}
		throw new RuntimeException();
	}

	public DropPool getDropPool() {
		return dropPool;
	}

	public PitNameTag getNameTag() {
		return nameTag;
	}

	public MobStatus getMobStatus() {
		return mobStatus;
	}

	public boolean isMinion() {
		return mobStatus.isMinion();
	}

	public boolean isThisMob(Entity entity) {
		return entity == getMob();
	}
}