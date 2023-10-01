package net.pitsim.spigot.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUIManager;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.misc.CustomSerializer;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class EnderchestPage {
	private transient StorageProfile profile;
	private transient Inventory inventory;
	private final int index;

	private ItemStack displayItem;
	private boolean isWardrobeEnabled;
	private final String[] itemsData = new String[StorageManager.ENDERCHEST_ITEM_SLOTS];
	private transient ItemStack[] items = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];

	public EnderchestPage(int index) {
		this.index = index;
		this.displayItem = new ItemStack(Material.ENDER_CHEST);
		this.isWardrobeEnabled = false;

		Arrays.fill(itemsData, "");
	}

	public void init(StorageProfile profile) {
		this.profile = profile;

		items = new ItemStack[StorageManager.ENDERCHEST_ITEM_SLOTS];

		for(int i = 0; i < items.length; i++) items[i] = StorageProfile.deserialize(itemsData[i], profile.getUniqueID());
		if(Misc.isAirOrNull(displayItem) || displayItem.getType() == Material.BARRIER) displayItem = new AItemStackBuilder(Material.ENDER_CHEST).getItemStack();

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

	public void save() {

		for(int i = 0; i < items.length; i++) {
			ItemStack item = inventory.getItem(i + 9);
			items[i] = (item == null ? new ItemStack(Material.AIR) : item);
		}

		for(int i = 0; i < items.length; i++) {
			String data = StorageProfile.serialize(profile.getOfflinePlayer(), items[i], false);
			itemsData[i] = data;
		}
	}

	public void writeData(PluginMessage message, boolean isLogout) {
		message.writeString(CustomSerializer.serialize(getDisplayItem()))
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
		return displayItem.clone();
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
