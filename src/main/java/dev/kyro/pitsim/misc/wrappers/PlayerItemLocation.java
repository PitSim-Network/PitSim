package dev.kyro.pitsim.misc.wrappers;

import java.util.ArrayList;
import java.util.List;

public class PlayerItemLocation {
	private static final List<PlayerItemLocation> slotLocations = new ArrayList<>();
	private static final PlayerItemLocation helmet = new PlayerItemLocation();
	private static final PlayerItemLocation chestplate = new PlayerItemLocation();
	private static final PlayerItemLocation leggings = new PlayerItemLocation();
	private static final PlayerItemLocation boots = new PlayerItemLocation();

	public int slot;

	static {
		for(int i = 0; i < 36; i++) slotLocations.add(new PlayerItemLocation(i));
	}

	private PlayerItemLocation() {}

	private PlayerItemLocation(int slot) {
		this.slot = slot;
	}

	public static PlayerItemLocation slot(int slot) {
		return slotLocations.get(slot);
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
}
