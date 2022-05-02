package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.FermentedSpiderEye;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PitCaveSpider extends PitMob {

	public PitCaveSpider(Location spawnLoc) {
		super(MobType.CAVE_SPIDER, spawnLoc, 5, "&cCave Spider");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		CaveSpider caveSpider = (CaveSpider) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.CAVE_SPIDER);

		caveSpider.setMaxHealth(50);
		caveSpider.setHealth(50);
		caveSpider.setRemoveWhenFarAway(false);

		caveSpider.setCustomNameVisible(false);
		MobManager.makeTag(caveSpider, displayName);
		return caveSpider;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(FermentedSpiderEye.INSTANCE.getItem(), 50);
		return drops;
	}
}
