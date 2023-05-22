package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.SoulBranch;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.boosters.SoulBooster;
import dev.kyro.pitsim.controllers.OutpostManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.uncommon.Reaper;
import dev.kyro.pitsim.enums.MobStatus;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.holograms.Hologram;
import dev.kyro.pitsim.holograms.RefreshMode;
import dev.kyro.pitsim.holograms.ViewMode;
import dev.kyro.pitsim.misc.HypixelSound;
import dev.kyro.pitsim.misc.effects.SelectiveDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

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
		if(mob.getPassenger() != null) mob.getPassenger().remove();
		if(mob.getVehicle() != null) mob.getVehicle().remove();
		mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, getSpeedAmplifier(), true, false));
		mob.setMaxHealth(getMaxHealth());
		mob.setHealth(getMaxHealth());

		nameTag = createNameTag();
		nameTag.attach();

		onSpawn();
	}

	public void kill(PitPlayer pitKiller) {
		Player killer = pitKiller == null ? null : pitKiller.player;
		LinkedHashMap<UUID, Double> damageMap = new LinkedHashMap<>();
		if(killer != null) damageMap.put(killer.getUniqueId(), 1.0);
		if(mobStatus == MobStatus.STANDARD) {
			dropPool.mobDistribution(killer, this);

//			Multiplicative modifiers
			double soulChance = 0.05;
			if(pitKiller != null) soulChance *= DarkzoneManager.getSoulSoftCooldownMultiplier(pitKiller.player);

//			Additive modifiers
			double reaperChance = Reaper.getSoulChanceIncrease(killer) / 100.0;
			double soulBranchUpgrade = ProgressionManager.getUnlockedEffectAsValue(
					pitKiller, SoulBranch.INSTANCE, SkillBranch.PathPosition.FIRST_PATH, "soul-chance-mobs") / 100.0;

			soulChance *= 1 + (reaperChance + soulBranchUpgrade);
			if(Math.random() < soulChance) {
				double droppedSouls = getDroppedSouls();
				if(SoulBooster.INSTANCE.isActive()) droppedSouls *= 1 + (SoulBooster.getSoulsIncrease() / 100.0);
				DarkzoneManager.createSoulExplosion(damageMap,
						getMob().getLocation().add(0, 0.5, 0), (int) droppedSouls, false);
			}

			if(pitKiller != null) {
				double freshChance = 2 / 100.0;
				if(OutpostManager.shouldReceiveRewards(killer)) freshChance *= 1 + (OutpostManager.getOutpostFreshIncrease() / 100.0);

				if(DarkzoneManager.freshSoftCooldownList.contains(pitKiller.player.getUniqueId())) freshChance *= 0.1;
				freshChance *= 1 + (ProgressionManager.getUnlockedEffectAsValue(
						pitKiller, SoulBranch.INSTANCE, SkillBranch.PathPosition.SECOND_PATH, "fresh-chance") / 100.0);
				if(Math.random() < freshChance) {
					MysticType mysticType = Math.random() < 0.5 ? MysticType.TAINTED_SCYTHE : MysticType.TAINTED_CHESTPLATE;
					ItemStack dropStack = MysticFactory.getFreshItem(mysticType, null);
					HypixelSound.play(pitKiller.player, mob.getLocation(), HypixelSound.Sound.FRESH_DROP);
					DarkzoneManager.putOnSoftFreshCooldown(pitKiller.player);

					SelectiveDrop selectiveDrop = new SelectiveDrop(dropStack, mob.getLocation());
					selectiveDrop.addPlayer(pitKiller.player);
					selectiveDrop.dropItem();

					Hologram hologram = new Hologram(mob.getLocation(), ViewMode.SELECT, RefreshMode.MANUAL) {
						@Override
						public List<String> getStrings(Player player) {
							return Collections.singletonList(dropStack.getItemMeta().getDisplayName());
						}
					};

					new BukkitRunnable() {
						@Override
						public void run() {
							if(selectiveDrop.getDroppedItem() == null) return;

							if(selectiveDrop.getDroppedItem().isDead()) {
								hologram.remove();
								return;
							}

							hologram.teleport(selectiveDrop.getDroppedItem().getLocation().add(0, 0.5, 0));
							hologram.addPermittedViewer(pitKiller.player);
						}
					}.runTaskLater(PitSim.INSTANCE, 20);

					selectiveDrop.setCallBack(new BukkitRunnable() {
						@Override
						public void run() {
							hologram.remove();
						}
					});

					new BukkitRunnable() {
						@Override
						public void run() {
							if(selectiveDrop.getDroppedItem().isDead()) return;
							for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
								hologram.addPermittedViewer(onlinePlayer);
								selectiveDrop.addPlayer(onlinePlayer);
							}
						}
					}.runTaskLater(PitSim.INSTANCE, 20 * 10);


//					FakeItem fakeItem = new FakeItem(dropStack, mob.getLocation())
//							.removeAfter(20 * 60)
//							.onPickup((pickupPlayer, itemStack) -> {
//								pickupPlayer.getInventory().addItem(itemStack);
//								pickupPlayer.updateInventory();
//							})
//							.addViewer(pitKiller.player);
//
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							fakeItem.showToAllPlayers();
//						}
//					}.runTaskLater(PitSim.INSTANCE, 20 * 10);
				}
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