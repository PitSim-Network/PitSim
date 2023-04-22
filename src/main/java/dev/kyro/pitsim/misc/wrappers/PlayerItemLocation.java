package dev.kyro.pitsim.misc.wrappers;

import dev.kyro.pitsim.storage.StorageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerItemLocation {
	private static final List<PlayerItemLocation> itemLocations = new ArrayList<>();

	private static final List<PlayerItemLocation> inventorySlotLocations = new ArrayList<>();
	private static final PlayerItemLocation helmet = new PlayerItemLocation("helmet");
	private static final PlayerItemLocation chestplate = new PlayerItemLocation("chestplate");
	private static final PlayerItemLocation leggings = new PlayerItemLocation("leggings");
	private static final PlayerItemLocation boots = new PlayerItemLocation("boots");
	private static final Map<Integer, List<PlayerItemLocation>> enderchestSlotLocations = new HashMap<>();

	public String identifier;

	static {
		for(int i = 0; i < 36; i++) inventorySlotLocations.add(new PlayerItemLocation("inventory_"));
		for(int i = 0; i < StorageManager.MAX_ENDERCHEST_PAGES; i++) {
			List<PlayerItemLocation> pageItems = new ArrayList<>();
			for(int j = 0; j < StorageManager.ENDERCHEST_ITEM_SLOTS; j++)
				pageItems.add(new PlayerItemLocation("enderchest_" + i + "_item_" + j));
			enderchestSlotLocations.put(i, pageItems);
		}
	}

	private PlayerItemLocation(String identifier) {
		this.identifier = identifier;

		itemLocations.add(this);
	}

	public static PlayerItemLocation slot(int slot) {
		return inventorySlotLocations.get(slot);
	}

	public static PlayerItemLocation helmet() {
		return helmet;
	}

	public static PlayerItemLocation chestplate() {
		return chestplate;
	}

	public static PlayerItemLocation leggings() {
		return leggings;
	}

	public static PlayerItemLocation boots() {
		return boots;
	}

	public static PlayerItemLocation enderchest(int index, int slot) {
		return enderchestSlotLocations.get(index).get(slot);
	}

	public static List<PlayerItemLocation> getAllLocations() {
		return itemLocations;
	}

	public static PlayerItemLocation getLocation(String identifier) {
		for(PlayerItemLocation itemLocation : itemLocations) if(itemLocation.identifier.equals(identifier)) return itemLocation;
		throw new RuntimeException();
	}
}
