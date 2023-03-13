package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.Charcoal;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

public class PitWitherSkeleton extends PitMob {

	public PitWitherSkeleton(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Skeleton witherSkeleton = spawnLocation.getWorld().spawn(spawnLocation, Skeleton.class);
		witherSkeleton.setCustomNameVisible(false);
		witherSkeleton.setRemoveWhenFarAway(false);
		witherSkeleton.setCanPickupItems(false);

		witherSkeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(witherSkeleton);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return witherSkeleton;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Wither Skeleton" : "Wither Skeleton";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_GRAY;
	}

	@Override
	public int getMaxHealth() {
		return 140;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return 7;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(Charcoal.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}