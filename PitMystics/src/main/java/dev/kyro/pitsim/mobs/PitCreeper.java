package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.Gunpowder;
import dev.kyro.pitsim.brewing.ingredients.SpiderEye;
import dev.kyro.pitsim.controllers.MobManager;
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
		super(MobType.CHARGED_CREEPER, spawnLoc, 4, 10, "&cCreeper", 7);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Creeper creeper = (Creeper) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.CREEPER);

		creeper.setMaxHealth(50);
		creeper.setHealth(50);
		creeper.setPowered(true);
		creeper.setRemoveWhenFarAway(false);

		creeper.setCustomNameVisible(false);
		MobManager.makeTag(creeper, displayName);
		return creeper;
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Creeper)) return;

		PitMob mob = PitMob.getPitMob((LivingEntity) entity);
		if(mob == null) return;
		MobManager.mobs.remove(mob);
		MobManager.nameTags.get(mob.entity.getUniqueId()).remove();
		MobManager.nameTags.remove(mob.entity.getUniqueId());
		event.setRadius(0);

		for (Entity player : entity.getNearbyEntities(5, 5, 5)) {
			if(!(player instanceof Player)) continue;

			PitPlayer.getPitPlayer((Player) player).damage(mob.damage, (LivingEntity) entity);
		}
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(Gunpowder.INSTANCE.getItem(), 20);

		return drops;
	}
}
