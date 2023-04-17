package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.gui.AGUIManager;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnderchestPage {
	private final StorageProfile profile;
	private Inventory inventory;
	private final int index;

	private String name;
	private ItemStack displayItem;
	private boolean isWardrobeEnabled;
	private final ItemStack[] items = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];

	public EnderchestPage(StorageProfile profile, PluginMessage message) {
		this.profile = profile;

		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();

		index = integers.remove(0);
		name = strings.remove(0);
		if(name.isEmpty()) name = "&5&lENDERCHEST &7Page " + (index + 1);
		displayItem = CustomSerializer.deserializeDirectly(strings.remove(0));
		if(Misc.isAirOrNull(displayItem)) displayItem = new AItemStackBuilder(Material.ENDER_CHEST).getItemStack();
		isWardrobeEnabled = booleans.remove(0);
		for(int i = 0; i < items.length; i++) items[i] = StorageProfile.deserialize(strings.remove(0), profile.getUUID());

		createInventory(message);
	}

	public void createInventory(PluginMessage message) {
		List<String> strings = message.getStrings();

		this.inventory = PitSim.INSTANCE.getServer().createInventory(null, 45, "Enderchest - Page " + (index + 1));

		for(int i = 0; i < items.length; i++) inventory.setItem(i + 9, StorageProfile.deserialize(strings.remove(0), profile.getUUID()));

		ItemStack borderStack = new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, 15)
				.setName(" ")
				.getItemStack();

		for(int i = 0; i < inventory.getSize(); i++) {
			if(i > 9 && i < inventory.getSize() - 8) continue;
			inventory.setItem(i, borderStack);
		}

		inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS / 9 + 13, AGUIManager.getBackItemStack());
		if(index == 0)
			inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS / 9 + 9, AGUIManager.getPreviousPageItemStack());
		if(index == StorageManager.MAX_ENDERCHEST_PAGES)
			inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS / 9 + 17, AGUIManager.getNextPageItemStack());
	}

	public void writeData(PluginMessage message, boolean isLogout) {
		for(ItemStack item : items) message.writeString(StorageProfile.serialize(profile.getOfflinePlayer(), item, isLogout));
	}

	public int getItemCount() {
		int total = 0;
		for(ItemStack itemStack : items) if(!Misc.isAirOrNull(itemStack)) total++;
		return total;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}

	public boolean isWardrobeEnabled() {
		return isWardrobeEnabled;
	}

	public void setWardrobeEnabled(boolean hasWardrobeEnabled) {
		this.isWardrobeEnabled = hasWardrobeEnabled;
	}

	public ItemStack[] getItems() {
		return items;
	}
}
