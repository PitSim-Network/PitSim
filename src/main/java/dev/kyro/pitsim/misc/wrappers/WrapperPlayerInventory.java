package dev.kyro.pitsim.misc.wrappers;

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

		for(int i = 0; i < inventory.getSize(); i++) itemMap.put(PlayerItemLocation.slot(i), inventory.getItem(i));
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
			ItemStack itemStack = getItem(PlayerItemLocation.slot(i));
			inventory.setItem(i, itemStack);
		}
		inventory.setHelmet(getItem(PlayerItemLocation.helmet()));
		inventory.setChestplate(getItem(PlayerItemLocation.chestplate()));
		inventory.setLeggings(getItem(PlayerItemLocation.leggings()));
		inventory.setBoots(getItem(PlayerItemLocation.boots()));
		player.updateInventory();
	}
}
