package dev.kyro.pitsim.misc.wrappers;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryLocation {
	private static final List<PlayerInventoryLocation> allInventoryLocation = new ArrayList<>();

	public int slot;
	public boolean isHelmet;
	public boolean isChestplate;
	public boolean isLeggings;
	public boolean isBoots;

	static {
		for(int i = 0; i < 36; i++) allInventoryLocation.add(new PlayerInventoryLocation(i));

	}

	private PlayerInventoryLocation() {}

	public PlayerInventoryLocation(int slot) {
		this.slot = slot;
	}

	public static PlayerInventoryLocation asHelmet() {
		PlayerInventoryLocation location = new PlayerInventoryLocation();
		location.isHelmet = true;
		return location;
	}

	public static PlayerInventoryLocation asChestplate() {
		PlayerInventoryLocation location = new PlayerInventoryLocation();
		location.isChestplate = true;
		return location;
	}

	public static PlayerInventoryLocation asLeggings() {
		PlayerInventoryLocation location = new PlayerInventoryLocation();
		location.isLeggings = true;
		return location;
	}

	public static PlayerInventoryLocation asBoots() {
		PlayerInventoryLocation location = new PlayerInventoryLocation();
		location.isBoots = true;
		return location;
	}
}
