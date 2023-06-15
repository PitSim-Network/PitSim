package net.pitsim.pitsim.adarkzone.mobs;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.*;
import net.pitsim.pitsim.adarkzone.notdarkzone.PitEquipment;
import net.pitsim.pitsim.aitems.mobdrops.Bone;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

public class PitSkeleton extends PitMob {

	public PitSkeleton(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SKELETON;
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
				PitEquipment equipment = new PitEquipment();
				equipment.setEquipment(skeleton);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return skeleton;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Skeleton" : "Skeleton";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.GRAY;
	}

	@Override
	public int getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addRareItem(() -> ItemFactory.getItem(Bone.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}