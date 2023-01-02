package dev.kyro.pitsim.adarkzone.old.slayers.tainted;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GearType {
	DAMAGE(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), GearConstructor.damageLeggings(), new ItemStack(Material.DIAMOND_BOOTS), GearConstructor.damageSword()),
	TANK(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), GearConstructor.tankLeggings(), new ItemStack(Material.DIAMOND_BOOTS), GearConstructor.tankSword()),
	GLASS_CANNON(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), GearConstructor.glassLeggings(), new ItemStack(Material.DIAMOND_BOOTS), GearConstructor.glassSword()),
	MEDIUM(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), GearConstructor.mediumLeggings(), new ItemStack(Material.DIAMOND_BOOTS), GearConstructor.mediumSword()),
	SHREDDER(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), GearConstructor.shredderLeggings(), new ItemStack(Material.DIAMOND_BOOTS), GearConstructor.shredderSword());

	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public ItemStack sword;

	GearType(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack sword) {
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.sword = sword;
	}
}
