package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.FermentedSpiderEye;
import dev.kyro.pitsim.brewing.ingredients.MagmaCream;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PitMagmaCube extends PitMob {

	public PitMagmaCube(Location spawnLoc) {
		super(MobType.MAGMA_CUBE, spawnLoc, 6, "&cMagma Cube");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		MagmaCube magmaCube = (MagmaCube) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.MAGMA_CUBE);

		magmaCube.setMaxHealth(50);
		magmaCube.setHealth(50);
		magmaCube.setRemoveWhenFarAway(false);
		magmaCube.setSize(5);

		magmaCube.setCustomNameVisible(false);
		MobManager.makeTag(magmaCube, displayName);
		return magmaCube;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(MagmaCream.INSTANCE.getItem(), 50);
		return drops;
	}
}
