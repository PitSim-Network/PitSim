package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.EnderPearl;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OldPitEnderman extends OldPitMob {

	public OldPitEnderman(Location spawnLoc) {
		super(MobType.ENDERMAN, spawnLoc, 10, MobValues.endermanDamage, "&cEnderman", MobValues.endermanSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Enderman enderman = (Enderman) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);

		enderman.setMaxHealth(MobValues.endermanHealth);
		enderman.setHealth(MobValues.endermanHealth);

		enderman.setCustomNameVisible(false);
		enderman.setRemoveWhenFarAway(false);
		MobManager.makeTag(enderman, displayName);
		return enderman;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(EnderPearl.INSTANCE.getItem(), 2);

		return drops;
	}
}
