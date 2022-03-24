package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class PitZombie extends PitMob {

	public PitZombie(Location spawnLoc) {
		super(MobType.ZOMBIE, spawnLoc, 1, "&cZombie");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);

		zombie.setMaxHealth(50);
		zombie.setHealth(50);

		zombie.setCustomName(displayName);
		zombie.setCustomNameVisible(false);

		MobManager.makeTag(this);

		return zombie;
	}
}
