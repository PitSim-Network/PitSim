package dev.kyro.pitsim.adarkzone.notdarkzone;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PitEquipment {
	private ItemStack held = new ItemStack(Material.AIR);

	private ItemStack helmet = new ItemStack(Material.AIR);
	private ItemStack chestplate = new ItemStack(Material.AIR);
	private ItemStack leggings = new ItemStack(Material.AIR);
	private ItemStack boots = new ItemStack(Material.AIR);

	public PitEquipment() {}

	public PitEquipment(Player player) {
		if(player.getItemInHand() != null) this.held = player.getItemInHand();
		if(player.getEquipment().getHelmet() != null) this.helmet = player.getEquipment().getHelmet();
		if(player.getEquipment().getChestplate() != null) this.chestplate = player.getEquipment().getChestplate();
		if(player.getEquipment().getLeggings() != null) this.leggings = player.getEquipment().getLeggings();
		if(player.getEquipment().getBoots() != null) this.boots = player.getEquipment().getBoots();
	}

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

	public List<ItemStack> getAsList() {
		List<ItemStack> equipmentList = new ArrayList<>();
		equipmentList.add(held);
		equipmentList.add(helmet);
		equipmentList.add(chestplate);
		equipmentList.add(leggings);
		equipmentList.add(boots);
		return equipmentList;
	}

	public ItemStack getItemStack(EquipmentType equipmentType) {
		switch(equipmentType) {
			case HELD:
				return held;
			case HELMET:
				return helmet;
			case CHESTPLATE:
				return chestplate;
			case LEGGINGS:
				return leggings;
			case BOOTS:
				return boots;
		}
		throw new RuntimeException();
	}

	@Override
	public boolean equals(Object otherObject) {
		if(!(otherObject instanceof PitEquipment)) return false;
		PitEquipment otherEquipment = (PitEquipment) otherObject;
		return held.equals(otherEquipment.held) &&
				helmet.equals(otherEquipment.helmet) &&
				chestplate.equals(otherEquipment.chestplate) &&
				leggings.equals(otherEquipment.leggings) &&
				boots.equals(otherEquipment.boots);
	}
}
