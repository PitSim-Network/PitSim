package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class PitZombie extends PitMob {
	public static PitZombie INSTANCE;

	public PitZombie(Location spawnLoc) {
		super(MobType.ZOMBIE, spawnLoc, 1, "&cZombie");
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
		zombie.getEquipment().setArmorContents(new ItemStack[4]);
		MobManager.makeTag(zombie, displayName);
		return zombie;
	}
}
