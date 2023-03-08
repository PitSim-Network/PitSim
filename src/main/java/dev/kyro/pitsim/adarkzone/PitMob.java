package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.SoulBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.uncommon.Reaper;
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
	private MobClass mobClass;

	public PitMob(Location spawnLocation, MobClass mobClass) {
		if(spawnLocation == null) return;
		this.dropPool = createDropPool(mobClass);
		this.mobClass = mobClass;
		spawn(spawnLocation);
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract Creature createMob(Location spawnLocation);
	public abstract String getRawDisplayName(MobClass mobClass);
	public abstract ChatColor getChatColor(MobClass mobClass);
	public abstract int getMaxHealth(MobClass mobClass);
	public abstract int getSpeedAmplifier(MobClass mobClass);
	public abstract int getDroppedSouls(MobClass mobClass);
	public abstract DropPool createDropPool(MobClass mobClass);
	public abstract PitNameTag createNameTag(MobClass mobClass);

	//	Internal events (override to add functionality)
	public void onSpawn() {}
	public void onDeath() {}

	public String getRawDisplayNamePlural() {
		return getRawDisplayName(mobClass) + "s";
	}

	public String getDisplayName() {
		return getChatColor(mobClass) + getRawDisplayName(mobClass);
	}

	public String getDisplayNamePlural() {
		return getChatColor(mobClass) + getRawDisplayNamePlural();
	}

	public void spawn(Location spawnLocation) {
		mob = createMob(spawnLocation);
		if(mob.isInsideVehicle()) mob.getVehicle().remove();
		mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, getSpeedAmplifier(mobClass), true, false));
		mob.setMaxHealth(getMaxHealth(mobClass));
		mob.setHealth(getMaxHealth(mobClass));

		nameTag = createNameTag(mobClass);
		nameTag.attach();

		onSpawn();
	}

	public void kill(PitPlayer pitKiller) {
		dropPool.singleDistribution(pitKiller.player);

		double soulChance = 0.05;
		soulChance *= 1 + (Reaper.getSoulChanceIncrease(pitKiller.player) / 100.0);
		soulChance *= 1 + (ProgressionManager.getUnlockedEffectAsValue(
				pitKiller, SoulBranch.INSTANCE, SkillBranch.PathPosition.FIRST_PATH, "soul-chance-mobs") / 100.0);
		if(Math.random() < soulChance) DarkzoneManager.createSoulExplosion(pitKiller.player,
				getMob().getLocation().add(0, 0.5, 0), getDroppedSouls(mobClass), false);

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

	public boolean isThisMob(Entity entity) {
		return entity == getMob();
	}
}

enum MobClass {
	STANDARD,
	MINION;
}
