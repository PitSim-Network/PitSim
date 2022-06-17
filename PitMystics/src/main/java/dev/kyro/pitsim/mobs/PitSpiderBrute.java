package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.SpiderEye;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PitSpiderBrute extends PitMob {

	public PitSpiderBrute(Location spawnLoc) {
		super(MobType.SPIDER, spawnLoc, 5, 14,  "&c&lSpider Brute");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Spider spider = (Spider) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SPIDER);

		spider.setMaxHealth(50);
		spider.setHealth(50);
		spider.setRemoveWhenFarAway(false);

		spider.setCustomNameVisible(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(spider, PotionEffectType.SPEED, 60 * 20 * 10, 4, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 2);

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
