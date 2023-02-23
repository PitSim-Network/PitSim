package dev.kyro.pitsim.misc.wrappers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventoryWrapper {
	public Player player;
	public PlayerInventory inventory;
	private final Map<PlayerInventoryLocation, ItemStack> itemMap = new LinkedHashMap<>();

	public PlayerInventoryWrapper(Player player) {
		this.player = player;
		this.inventory = player.getInventory();

		for(int i = 0; i < inventory.getSize(); i++) itemMap.put(PlayerInventoryLocation.slot(i), inventory.getItem(i));
		itemMap.put(PlayerInventoryLocation.helmet(), inventory.getHelmet());
		itemMap.put(PlayerInventoryLocation.chestplate(), inventory.getChestplate());
		itemMap.put(PlayerInventoryLocation.leggings(), inventory.getLeggings());
		itemMap.put(PlayerInventoryLocation.boots(), inventory.getBoots());
	}

	public Map<PlayerInventoryLocation, ItemStack> getItemMap() {
		return itemMap;
	}

	public ItemStack getItem(PlayerInventoryLocation location) {
		return itemMap.get(location);
	}

	public List<ItemStack> getAllItems() {
		return new ArrayList<>(itemMap.values());
	}

	public void putItem(PlayerInventoryLocation inventoryLocation, ItemStack itemStack) {
		itemMap.put(inventoryLocation, itemStack);
	}

	public void removeItem(PlayerInventoryLocation inventoryLocation) {
		itemMap.remove(inventoryLocation);
	}

	public void setInventory() {
		for(int i = 0; i < 36; i++) {
			ItemStack itemStack = getItem(PlayerInventoryLocation.slot(i));
			inventory.setItem(i, itemStack);
		}
		inventory.setHelmet(getItem(PlayerInventoryLocation.helmet()));
		inventory.setChestplate(getItem(PlayerInventoryLocation.chestplate()));
		inventory.setLeggings(getItem(PlayerInventoryLocation.leggings()));
		inventory.setBoots(getItem(PlayerInventoryLocation.boots()));
		player.updateInventory();
	}
}
