package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.RawPork;
import dev.kyro.pitsim.brewing.ingredients.RottenFlesh;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PitZombiePigman extends PitMob {

	public PitZombiePigman(Location spawnLoc) {
		super(MobType.ZOMBIE_PIGMAN, spawnLoc, 7, 14, "&cZombie Pigman", 3);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		PigZombie zombiePigman = (PigZombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.PIG_ZOMBIE);

		zombiePigman.setMaxHealth(50);
		zombiePigman.setHealth(50);
		zombiePigman.setAngry(true);
		zombiePigman.setCustomNameVisible(false);
		zombiePigman.setRemoveWhenFarAway(false);
		zombiePigman.setBaby(false);
		MobManager.makeTag(zombiePigman, displayName);
		return zombiePigman;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(RawPork.INSTANCE.getItem(), 8);

		return drops;
	}
}
