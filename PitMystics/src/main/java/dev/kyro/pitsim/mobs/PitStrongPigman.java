package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.RawPork;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PitStrongPigman extends PitMob {

	public PitStrongPigman(Location spawnLoc) {
		super(MobType.ZOMBIE_PIGMAN, spawnLoc, 7, 30, "&c&lStrong Pigman");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		PigZombie zombiePigman = (PigZombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.PIG_ZOMBIE);

		zombiePigman.setMaxHealth(10);
		zombiePigman.setHealth(10);

		zombiePigman.setCustomNameVisible(false);
		zombiePigman.setRemoveWhenFarAway(false);
		zombiePigman.setBaby(false);
		zombiePigman.setAngry(true);
		new BukkitRunnable() {
			@Override
			public void run() {
				zombiePigman.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
				zombiePigman.getEquipment().setHelmetDropChance(0);
				zombiePigman.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
				zombiePigman.getEquipment().setChestplateDropChance(0);
				zombiePigman.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
				zombiePigman.getEquipment().setLeggingsDropChance(0);
				zombiePigman.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
				zombiePigman.getEquipment().setBootsDropChance(0);
				zombiePigman.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
				zombiePigman.getEquipment().setItemInHandDropChance(0);

				Misc.applyPotionEffect(zombiePigman, PotionEffectType.SPEED, 20 * 60 * 10, 4, false, false);
			}
		}.runTaskLater(PitSim.INSTANCE, 2);


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
