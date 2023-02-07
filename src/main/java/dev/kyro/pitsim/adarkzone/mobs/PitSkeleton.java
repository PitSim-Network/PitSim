package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.Bone;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

public class PitSkeleton extends PitMob {

	public PitSkeleton(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Skeleton skeleton = spawnLocation.getWorld().spawn(spawnLocation, Skeleton.class);
		skeleton.setCustomNameVisible(false);
		skeleton.setRemoveWhenFarAway(false);
		skeleton.setCanPickupItems(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(skeleton);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return skeleton;
	}

	@Override
	public String getRawDisplayName() {
		return "Skeleton";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.GRAY;
	}

	@Override
	public int getMaxHealth() {
		return 40;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public double getOffsetHeight() {
		return 1.5;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(Bone.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}