package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class PitSkeleton extends PitMob {

	public static PitSkeleton INSTANCE;

	public PitSkeleton(Location spawnLoc) {
		super(MobType.SKELETON, spawnLoc, 1, "&cSkeleton");
		INSTANCE = this;
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Skeleton skeleton = (Skeleton) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);

		skeleton.setMaxHealth(50);
		skeleton.setHealth(50);
		skeleton.setRemoveWhenFarAway(false);
		skeleton.getEquipment().setArmorContents(new ItemStack[4]);
		skeleton.setCustomNameVisible(false);
		MobManager.makeTag(skeleton, displayName);
		return skeleton;
	}
}
