package net.pitsim.pitsim.misc;

import net.pitsim.pitsim.storage.EnderchestPage;
import net.pitsim.pitsim.storage.StorageManager;
import net.pitsim.pitsim.storage.StorageProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

// needs to be (mostly) mirrored in the proxy plugin
public class PlayerItemLocation {
	private static final List<PlayerItemLocation> itemLocations = new ArrayList<>();

	private static final List<PlayerItemLocation> inventorySlotLocations = new ArrayList<>();
	private static final List<PlayerItemLocation> armorSlotLocations = new ArrayList<>();
	private static final Map<Integer, List<PlayerItemLocation>> enderchestSlotLocations = new HashMap<>();

	public Location location;
	public int page;
	public int slot;

	static {
		for(int i = 0; i < 36; i++) new PlayerItemLocation(Location.INVENTORY, i);
		for(int i = 0; i < 4; i++) new PlayerItemLocation(Location.ARMOR, i);
		for(int i = 0; i < StorageManager.MAX_ENDERCHEST_PAGES; i++)
			for(int j = 0; j < StorageManager.ENDERCHEST_ITEM_SLOTS; j++) new PlayerItemLocation(Location.ENDERCHEST, i, j);
	}

	private PlayerItemLocation(Location location, int slot) {
		this(location, -1, slot);
	}

	private PlayerItemLocation(Location location, int page, int slot) {
		this.location = location;
		this.page = page;
		this.slot = slot;

		switch(location) {
			case INVENTORY:
				inventorySlotLocations.add(this);
				break;
			case ARMOR:
				armorSlotLocations.add(this);
				break;
			case ENDERCHEST:
				enderchestSlotLocations.putIfAbsent(page, new ArrayList<>());
				enderchestSlotLocations.get(page).add(this);
				break;
		}
		itemLocations.add(this);
	}

	public static PlayerItemLocation inventory(int slot) {
		return inventorySlotLocations.get(slot);
	}

	public static PlayerItemLocation armor(int slot) {
		return armorSlotLocations.get(slot);
	}

	public static PlayerItemLocation helmet() {
		return armorSlotLocations.get(3);
	}

	public static PlayerItemLocation chestplate() {
		return armorSlotLocations.get(2);
	}

	public static PlayerItemLocation leggings() {
		return armorSlotLocations.get(1);
	}

	public static PlayerItemLocation boots() {
		return armorSlotLocations.get(0);
	}

	public static List<PlayerItemLocation> enderchest(int index) {
		return enderchestSlotLocations.get(index);
	}

	public static PlayerItemLocation enderchest(int index, int slot) {
		return enderchestSlotLocations.get(index).get(slot);
	}

	public static List<PlayerItemLocation> getLocations(Location... locations) {
		List<Location> locationsAsList = Arrays.asList(locations);
		List<PlayerItemLocation> itemLocations = new ArrayList<>();
		for(PlayerItemLocation itemLocation : PlayerItemLocation.itemLocations) {
			if(locations.length != 0 && !locationsAsList.contains(itemLocation.location)) continue;
			itemLocations.add(itemLocation);
		}
		return itemLocations;
	}

	public static PlayerItemLocation getLocation(String identifier) {
		for(PlayerItemLocation itemLocation : itemLocations) if(itemLocation.getIdentifier().equals(identifier)) return itemLocation;
		throw new RuntimeException();
	}

	public String getIdentifier() {
		List<String> attributes = new ArrayList<>();
		attributes.add(location.name().toLowerCase());
		if(page != -1) attributes.add(page + "");
		if(slot != -1) attributes.add(slot + "");
		return String.join("_", attributes);
	}

	public ItemStack getItem(UUID uuid, boolean useOnlinePlayer) {
		Player player = Bukkit.getPlayer(uuid);
		StorageProfile profile = StorageManager.getProfile(uuid);
		ItemStack itemStack;
		switch(location) {
			case INVENTORY:
				itemStack = useOnlinePlayer ? player.getInventory().getItem(slot) : profile.getInventory()[slot];
				break;
			case ARMOR:
				itemStack = useOnlinePlayer ? player.getInventory().getArmorContents()[slot] : profile.getArmor()[slot];
				break;
			case ENDERCHEST:
				EnderchestPage enderchestPage = profile.getEnderchestPage(page);
				itemStack = useOnlinePlayer ? enderchestPage.getInventory().getItem(slot + 9) : enderchestPage.getItems()[slot];
				break;
			default:
				throw new RuntimeException();
		}
		if(itemStack == null) itemStack = new ItemStack(Material.AIR);
		return itemStack;
	}

	public void setItem(UUID uuid, ItemStack itemStack, boolean useOnlinePlayer) {
		Player player = Bukkit.getPlayer(uuid);
		StorageProfile profile = StorageManager.getProfile(uuid);
		switch(location) {
			case INVENTORY:
				if(useOnlinePlayer) {
					player.getInventory().setItem(slot, itemStack);
				} else {
					profile.getInventory()[slot] = itemStack;
				}
				break;
			case ARMOR:
				if(useOnlinePlayer) {
					ItemStack[] armor = player.getInventory().getArmorContents();
					armor[slot] = itemStack;
					player.getInventory().setArmorContents(armor);
				} else {
					profile.getArmor()[slot] = itemStack;
				}
				break;
			case ENDERCHEST:
				EnderchestPage enderchestPage = profile.getEnderchestPage(page);
				enderchestPage.getInventory().setItem(slot + 9, itemStack);
		}
	}

	public enum Location {
		INVENTORY,
		ARMOR,
		ENDERCHEST
	}
}
