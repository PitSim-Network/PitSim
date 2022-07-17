package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.Coal;
import dev.kyro.pitsim.brewing.ingredients.RawPork;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PitWitherSkeleton extends PitMob {

	public PitWitherSkeleton(Location spawnLoc) {
		super(MobType.SKELETON, spawnLoc, 8, MobValues.witherSkeletonDamage, "&cWither Skeleton", MobValues.witherSkeletonSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Skeleton witherSkeleton = (Skeleton) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);

		witherSkeleton.setMaxHealth(MobValues.witherSkeletonHealth);
		witherSkeleton.setHealth(MobValues.witherSkeletonHealth);
		witherSkeleton.setRemoveWhenFarAway(false);

		witherSkeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);

		witherSkeleton.setCustomNameVisible(false);
		MobManager.makeTag(witherSkeleton, displayName);

		new BukkitRunnable() {
			@Override
			public void run() {
				witherSkeleton.getEquipment().clear();
			}
		}.runTaskLater(PitSim.INSTANCE, 2);

		return witherSkeleton;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(Coal.INSTANCE.getItem(), 6);

		return drops;
	}
}
