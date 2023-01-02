package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.MagmaCream;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class OldPitMagmaCube extends OldPitMob {

	public OldPitMagmaCube(Location spawnLoc) {
		super(MobType.MAGMA_CUBE, spawnLoc, 6, MobValues.magmaCubeDamage, "&cMagma Cube", MobValues.magmaCubeSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		MagmaCube magmaCube = (MagmaCube) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.MAGMA_CUBE);

		magmaCube.setSize(5);
		magmaCube.setRemoveWhenFarAway(false);
		magmaCube.setCustomNameVisible(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				magmaCube.setMaxHealth(MobValues.magmaCubeHealth);
				magmaCube.setHealth(MobValues.magmaCubeHealth);
			}
		}.runTaskLater(PitSim.INSTANCE, 5);


		MobManager.makeTag(magmaCube, displayName);
		return magmaCube;
	}

	@EventHandler
	public void onMagmaCubeAttack(AttackEvent.Apply event) {
		if(event.getAttacker() instanceof Player) return;
		OldPitMob mob = OldPitMob.getPitMob(event.getAttacker());
		if(mob == null) return;

		if(!(mob instanceof OldPitMagmaCube)) return;

		if(event.isFakeHit()) return;

		event.increase = mob.damage;

	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(MagmaCream.INSTANCE.getItem(), 10);
		return drops;
	}
}
