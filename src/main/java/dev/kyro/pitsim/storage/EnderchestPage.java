package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
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

	private ItemStack displayItem;
	private boolean isWardrobeEnabled;
	private final ItemStack[] items = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];

	public EnderchestPage(StorageProfile profile, PluginMessage message) {
		this.profile = profile;

		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();

		index = integers.remove(0);
		displayItem = CustomSerializer.deserializeDirectly(strings.remove(0));
		if(Misc.isAirOrNull(displayItem)) displayItem = new AItemStackBuilder(Material.ENDER_CHEST).getItemStack();
		isWardrobeEnabled = booleans.remove(0);
		for(int i = 0; i < items.length; i++) items[i] = StorageProfile.deserialize(strings.remove(0), profile.getUniqueID());

		createInventory();
	}

	public void createInventory() {
		this.inventory = PitSim.INSTANCE.getServer().createInventory(null,
				StorageManager.ENDERCHEST_ITEM_SLOTS + 18, "Enderchest - Page " + (index + 1));
		for(int i = 0; i < items.length; i++) inventory.setItem(i + 9, items[i]);

		ItemStack borderStack = new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, 15)
				.setName(" ")
				.getItemStack();

		for(int i = 0; i < inventory.getSize(); i++) {
			if(i >= 9 && i < inventory.getSize() - 9) continue;
			inventory.setItem(i, borderStack);
		}

		ItemStack homeStack = new AItemStackBuilder(Material.COMPASS, index + 1)
				.setName("&5Back to Menu")
				.setLore(new ALoreBuilder(
						"&7Click to return to",
						"&7the main menu"
				)).getItemStack();
		inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS + 13, homeStack);
		if(index != 0)
			inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS + 9, AGUIManager.getPreviousPageItemStack());
		if(index + 1 != StorageManager.MAX_ENDERCHEST_PAGES)
			inventory.setItem(StorageManager.ENDERCHEST_ITEM_SLOTS + 17, AGUIManager.getNextPageItemStack());
	}

	public void writeData(PluginMessage message, boolean isLogout) {
		message.writeString(CustomSerializer.serialize(displayItem))
				.writeBoolean(isWardrobeEnabled);
		for(int i = 0; i < StorageManager.ENDERCHEST_ITEM_SLOTS; i++) {
			int inventorySlot = i + 9;
			ItemStack itemStack = inventory.getItem(inventorySlot);
			message.writeString(StorageProfile.serialize(profile.getOfflinePlayer(), itemStack, isLogout));
		}
	}

	public int getItemCount() {
		int total = 0;
		for(int i = 0; i < StorageManager.ENDERCHEST_ITEM_SLOTS; i++) {
			int inventorySlot = i + 9;
			ItemStack itemStack = inventory.getItem(inventorySlot);
			if(!Misc.isAirOrNull(itemStack)) total++;
		}
		return total;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getIndex() {
		return index;
	}

	public String getDisplayName() {
		return "&5&lENDERCHEST &7Page " + (getIndex() + 1);
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
