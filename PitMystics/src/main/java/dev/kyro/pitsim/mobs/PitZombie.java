package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.RottenFlesh;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public 	class PitZombie extends PitMob {
	public static PitZombie INSTANCE;

	public PitZombie(Location spawnLoc) {
		super(MobType.ZOMBIE, spawnLoc, 1, 5, "&cZombie", 2);
		INSTANCE = this;
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);

		zombie.setMaxHealth(50);
		zombie.setHealth(50);
//		zombie.setCustomName(displayName);
		zombie.setCustomNameVisible(false);
		zombie.setRemoveWhenFarAway(false);
		zombie.setBaby(false);
		zombie.setVillager(false);
		zombie.setCanPickupItems(false);
		MobManager.makeTag(zombie, displayName);
		new BukkitRunnable() {
			@Override
			public void run() {
				zombie.getEquipment().clear();
			}
		}.runTaskLater(PitSim.INSTANCE, 2);

		return zombie;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(RottenFlesh.INSTANCE.getItem(), 50);

		return drops;
	}
}
