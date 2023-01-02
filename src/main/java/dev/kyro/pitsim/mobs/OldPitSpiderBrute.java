package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.SpiderEye;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OldPitSpiderBrute extends OldPitMob {

	public OldPitSpiderBrute(Location spawnLoc) {
		super(MobType.SPIDER, spawnLoc, 5, 14, "&c&lSpider Brute", 5);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Spider spider = (Spider) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SPIDER);

		spider.setMaxHealth(50);
		spider.setHealth(50);
		spider.setRemoveWhenFarAway(false);

		spider.setCustomNameVisible(false);

		MobManager.makeTag(spider, displayName);
		return spider;


	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(SpiderEye.INSTANCE.getItem(), 30);

		return drops;
	}
}
