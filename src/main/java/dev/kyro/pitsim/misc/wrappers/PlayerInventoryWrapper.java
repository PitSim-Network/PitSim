package dev.kyro.pitsim.misc.wrappers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerInventoryWrapper {
	public PlayerInventory inventory;
	Map<PlayerInventoryLocation, ItemStack> itemMap = new LinkedHashMap<>();

	public PlayerInventoryWrapper(PlayerInventory inventory) {
		this.inventory = inventory;

		for(int i = 0; i < inventory.getSize(); i++) itemMap.put(new PlayerInventoryLocation(i), inventory.getItem(i));
		itemMap.put(PlayerInventoryLocation.asHelmet(), inventory.getHelmet());
		itemMap.put(PlayerInventoryLocation.asChestplate(), inventory.getChestplate());
		itemMap.put(PlayerInventoryLocation.asLeggings(), inventory.getLeggings());
		itemMap.put(PlayerInventoryLocation.asBoots(), inventory.getBoots());
	}

	public void getItem(PlayerInventoryLocation location) {
		itemMap.get(location);
	}

	public void setItem(PlayerInventoryLocation inventoryLocation, ItemStack itemStack) {
	}
}
