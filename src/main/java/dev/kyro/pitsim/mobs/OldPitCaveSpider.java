package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.FermentedSpiderEye;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OldPitCaveSpider extends OldPitMob {

	public OldPitCaveSpider(Location spawnLoc) {
		super(MobType.CAVE_SPIDER, spawnLoc, 5, MobValues.caveSpiderDamage, "&cCave Spider", MobValues.caveSpiderSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		CaveSpider caveSpider = (CaveSpider) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.CAVE_SPIDER);

		caveSpider.setMaxHealth(MobValues.caveSpiderHealth);
		caveSpider.setHealth(MobValues.caveSpiderHealth);
		caveSpider.setRemoveWhenFarAway(false);

		caveSpider.setCustomNameVisible(false);
		MobManager.makeTag(caveSpider, displayName);
		return caveSpider;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(FermentedSpiderEye.INSTANCE.getItem(), 15);
		return drops;
	}
}
