package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.Gunpowder;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PitCreeper extends PitMob {

	public PitCreeper(Location spawnLoc) {
		super(MobType.CHARGED_CREEPER, spawnLoc, 4, MobValues.creeperDamage, "&cCreeper", MobValues.creeperSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Creeper creeper = (Creeper) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.CREEPER);

		creeper.setMaxHealth(MobValues.creeperHealth);
		creeper.setHealth(MobValues.creeperHealth);
		creeper.setPowered(true);
		creeper.setRemoveWhenFarAway(false);

		creeper.setCustomNameVisible(false);
		MobManager.makeTag(creeper, displayName);
		return creeper;
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Creeper)) return;

		PitMob mob = PitMob.getPitMob((LivingEntity) entity);
		if(mob == null) return;
		MobManager.mobs.remove(mob);
		MobManager.nameTags.get(mob.entity.getUniqueId()).remove();
		MobManager.nameTags.remove(mob.entity.getUniqueId());
		event.setRadius(0);

		for(Entity testEntity : entity.getNearbyEntities(7, 7, 7)) {
			if(!(testEntity instanceof Player)) continue;
			Player player = (Player) testEntity;
			if(NonManager.getNon(player) != null) continue;

			double distance = testEntity.getLocation().distance(player.getLocation());
			if(distance > 7) continue;
			distance -= 2;
			distance = Math.max(distance, 0);
			double multiplier = 1 - distance * 0.2;

			PitPlayer.getPitPlayer((Player) testEntity).damage(mob.damage * multiplier, (LivingEntity) entity);
		}
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(Gunpowder.INSTANCE.getItem(), 20);

		return drops;
	}
}
