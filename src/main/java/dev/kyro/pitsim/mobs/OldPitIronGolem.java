package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.IronIngot;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OldPitIronGolem extends OldPitMob {

	public OldPitIronGolem(Location spawnLoc) {
		super(MobType.IRON_GOLEM, spawnLoc, 9, MobValues.golemDamage, "&cIron Golem", MobValues.golemSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		IronGolem ironGolem = (IronGolem) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.IRON_GOLEM);

		ironGolem.setMaxHealth(MobValues.golemHealth);
		ironGolem.setHealth(MobValues.golemHealth);
		ironGolem.setRemoveWhenFarAway(false);

		ironGolem.setCustomNameVisible(false);
		MobManager.makeTag(ironGolem, displayName);
		return ironGolem;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(IronIngot.INSTANCE.getItem(), 4);

		return drops;
	}
}
