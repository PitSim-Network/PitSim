package dev.kyro.pitsim.mobs;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		drops.put(getFlesh(), 50);

		return drops;
	}

	public ItemStack getFlesh() {
		ItemStack flesh = new ItemStack(Material.ROTTEN_FLESH);
		ItemMeta meta = flesh.getItemMeta();
		List<String> lore = Arrays.asList(ChatColor.GRAY + "Flesh gathered from the Zombies", ChatColor.GRAY
				+ "of the Zombie Caves", "", ChatColor.DARK_PURPLE + "Tainted Item");
		meta.setLore(lore);
		meta.setDisplayName(ChatColor.GREEN + "Rotten Flesh");
		flesh.setItemMeta(meta);

		NBTItem nbtItem = new NBTItem(flesh);
		nbtItem.setBoolean(NBTTag.ZOMBIE_FLESH.getRef(), true);
		return nbtItem.getItem();
	}
}
