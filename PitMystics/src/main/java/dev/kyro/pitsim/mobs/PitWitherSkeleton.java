package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PitWitherSkeleton extends PitMob {

	public PitWitherSkeleton(Location spawnLoc) {
		super(MobType.WITHER_SKELETON, spawnLoc, 1, "&cWither Skeleton");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		WitherSkeleton witherSkeleton = (WitherSkeleton) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);

		witherSkeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
		witherSkeleton.setMaxHealth(50);
		witherSkeleton.setHealth(50);

		witherSkeleton.setCustomNameVisible(false);
		MobManager.makeTag(witherSkeleton, displayName);
		return witherSkeleton;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		return null;
	}
}
