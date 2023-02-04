package dev.kyro.pitsim.adarkzone.notdarkzone;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PitEquipment {
	private ItemStack held;

	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;

	public PitEquipment() {}

	public PitEquipment held(ItemStack held) {
		this.held = held;
		return this;
	}

	public PitEquipment helmet(ItemStack helmet) {
		this.helmet = helmet;
		return this;
	}

	public PitEquipment chestplate(ItemStack chestplate) {
		this.chestplate = chestplate;
		return this;
	}

	public PitEquipment leggings(ItemStack leggings) {
		this.leggings = leggings;
		return this;
	}

	public PitEquipment boots(ItemStack boots) {
		this.boots = boots;
		return this;
	}

	public void setHeld(LivingEntity entity) {
		entity.getEquipment().setItemInHand(held);
		if(entity instanceof Player) ((Player) entity).updateInventory();
	}

	public void setEquipment(LivingEntity entity) {
		setHeld(entity);
		entity.getEquipment().setHelmet(helmet);
		entity.getEquipment().setChestplate(chestplate);
		entity.getEquipment().setLeggings(leggings);
		entity.getEquipment().setBoots(boots);
		if(entity instanceof Player) ((Player) entity).updateInventory();
	}

	public void setHeld(NPC npc) {
		Equipment equipment = npc.getOrAddTrait(Equipment.class);
		equipment.set(Equipment.EquipmentSlot.HAND, held);
	}

	public void setEquipment(NPC npc) {
		Equipment equipment = npc.getOrAddTrait(Equipment.class);
		equipment.set(Equipment.EquipmentSlot.HAND, held);
		equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
		equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chestplate);
		equipment.set(Equipment.EquipmentSlot.LEGGINGS, leggings);
		equipment.set(Equipment.EquipmentSlot.BOOTS, boots);
	}

	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
	}

	public void setChestplate(ItemStack chestplate) {
		this.chestplate = chestplate;
	}

	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
	}

	public void setBoots(ItemStack boots) {
		this.boots = boots;
	}
}
