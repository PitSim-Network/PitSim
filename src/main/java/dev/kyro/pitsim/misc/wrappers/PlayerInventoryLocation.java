package dev.kyro.pitsim.misc.wrappers;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryLocation {
	private static final List<PlayerInventoryLocation> slotLocations = new ArrayList<>();
	private static final PlayerInventoryLocation helmet = new PlayerInventoryLocation();
	private static final PlayerInventoryLocation chestplate = new PlayerInventoryLocation();
	private static final PlayerInventoryLocation leggings = new PlayerInventoryLocation();
	private static final PlayerInventoryLocation boots = new PlayerInventoryLocation();

	public int slot;

	static {
		for(int i = 0; i < 36; i++) slotLocations.add(new PlayerInventoryLocation(i));
	}

	private PlayerInventoryLocation() {}

	private PlayerInventoryLocation(int slot) {
		this.slot = slot;
	}

	public static PlayerInventoryLocation slot(int slot) {
		return slotLocations.get(slot);
	}

	public static PlayerInventoryLocation helmet() {
		return helmet;
	}

	public static PlayerInventoryLocation chestplate() {
		return chestplate;
	}

	public static PlayerInventoryLocation leggings() {
		return leggings;
	}

	public static PlayerInventoryLocation boots() {
		return boots;
	}
}
