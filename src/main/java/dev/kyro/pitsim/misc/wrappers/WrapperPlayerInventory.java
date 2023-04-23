package dev.kyro.pitsim.misc.wrappers;

import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PlayerItemLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WrapperPlayerInventory {
	public Player player;
	public PlayerInventory inventory;
	private final Map<PlayerItemLocation, ItemStack> itemMap = new LinkedHashMap<>();

	public WrapperPlayerInventory(Player player) {
		this.player = player;
		this.inventory = player.getInventory();

		for(int i = 0; i < inventory.getSize(); i++) itemMap.put(PlayerItemLocation.inventory(i), inventory.getItem(i));
		itemMap.put(PlayerItemLocation.helmet(), inventory.getHelmet());
		itemMap.put(PlayerItemLocation.chestplate(), inventory.getChestplate());
		itemMap.put(PlayerItemLocation.leggings(), inventory.getLeggings());
		itemMap.put(PlayerItemLocation.boots(), inventory.getBoots());
	}

	public Map<PlayerItemLocation, ItemStack> getItemMap() {
		return itemMap;
	}

	public ItemStack getItem(PlayerItemLocation location) {
		return itemMap.get(location);
	}

	public List<ItemStack> getAllItems() {
		return new ArrayList<>(itemMap.values());
	}

	public void putItem(PlayerItemLocation inventoryLocation, ItemStack itemStack) {
		itemMap.put(inventoryLocation, itemStack);
	}

	public void removeItem(PlayerItemLocation inventoryLocation) {
		itemMap.remove(inventoryLocation);
	}

	public void setInventory() {
		for(int i = 0; i < 36; i++) {
			ItemStack itemStack = getItem(PlayerItemLocation.inventory(i));
			if(isSameStack(itemStack, inventory.getItem(i))) continue;
			inventory.setItem(i, itemStack);
		}
		if(!isSameStack(inventory.getHelmet(), getItem(PlayerItemLocation.helmet()))) inventory.setHelmet(getItem(PlayerItemLocation.helmet()));
		if(!isSameStack(inventory.getChestplate(), getItem(PlayerItemLocation.chestplate()))) inventory.setChestplate(getItem(PlayerItemLocation.chestplate()));
		if(!isSameStack(inventory.getLeggings(), getItem(PlayerItemLocation.leggings()))) inventory.setLeggings(getItem(PlayerItemLocation.leggings()));
		if(!isSameStack(inventory.getBoots(), getItem(PlayerItemLocation.boots()))) inventory.setBoots(getItem(PlayerItemLocation.boots()));
		player.updateInventory();
	}

	public boolean isSameStack(ItemStack stack1, ItemStack stack2) {
		if(Misc.isAirOrNull(stack1) && Misc.isAirOrNull(stack2)) return true;
		if(stack1 == null || stack2 == null) return false;
		return stack1.equals(stack2);
	}
}
