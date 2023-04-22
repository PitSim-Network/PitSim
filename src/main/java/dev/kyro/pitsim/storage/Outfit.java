package dev.kyro.pitsim.storage;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.wrappers.PlayerItemLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Outfit {
	private final StorageProfile storageProfile;

	private final Map<PlayerItemLocation, String> itemUUIDMap = new HashMap<>();

	public Outfit(StorageProfile profile, PluginMessage message) {
		this.storageProfile = profile;

		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();

		for(PlayerItemLocation itemLocation : PlayerItemLocation.getAllLocations()) {

		}
	}
}
