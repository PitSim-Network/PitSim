package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketAdminPanel extends AGUIPanel {

	public UUID playerUUID;
	public List<UUID> storedListings = new ArrayList<>();
	public MarketAdminPanel(AGUI gui, UUID playerUUID) {
		super(gui);

		this.playerUUID = playerUUID;

		List<MarketListing> listings = MarketManager.getActiveListings(playerUUID);

		int i = 0;
		for(MarketListing listing : listings) {
			ItemStack item = listing.getItemStack();
			List<String> lore = item.getItemMeta().getLore();
			lore.remove(lore.size() - 1);
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Listing ID: &f" + listing.marketUUID));
			lore.add("");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&cClick to remove listing!"));
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);

			getInventory().setItem(i, item);
			storedListings.add(listing.marketUUID);
			i++;
		}
	}

	@Override
	public String getName() {
		return "Market Admin";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		int slot = event.getSlot();
		if(slot >= storedListings.size()) return;
		UUID listingID = storedListings.get(slot);
		MarketListing listing = MarketManager.getListing(listingID);
		if(listing == null) return;

		openPanel(new ConfirmStaffDeletionPanel(gui, listing));
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
