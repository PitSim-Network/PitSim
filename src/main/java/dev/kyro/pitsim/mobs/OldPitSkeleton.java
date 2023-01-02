package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.Bone;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class OldPitSkeleton extends OldPitMob {

	public static OldPitSkeleton INSTANCE;

	public OldPitSkeleton(Location spawnLoc) {
		super(MobType.SKELETON, spawnLoc, 2, MobValues.skeletonDamage, "&cSkeleton", MobValues.skeletonSpeed);
		INSTANCE = this;
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Skeleton skeleton = (Skeleton) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);

		skeleton.setMaxHealth(MobValues.skeletonHealth);
		skeleton.setHealth(MobValues.skeletonHealth);
		skeleton.setRemoveWhenFarAway(false);
		skeleton.setCustomNameVisible(false);
		skeleton.setCanPickupItems(false);
		MobManager.makeTag(skeleton, displayName);
		new BukkitRunnable() {
			@Override
			public void run() {
				skeleton.getEquipment().clear();
			}
		}.runTaskLater(PitSim.INSTANCE, 2);

		return skeleton;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(Bone.INSTANCE.getItem(), 40);

		return drops;
	}
}
