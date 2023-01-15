package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MarketSelectionPanel extends AGUIPanel {
	public MarketSelectionPanel(AGUI gui) {
		super(gui);

		AItemStackBuilder listingsBuilder = new AItemStackBuilder(Material.BOOK)
				.setName("&eView Market Listings")
				.setLore(new ALoreBuilder(
						"&7View all of the items",
						"&7currently being sold on",
						"&7the player market.",
						"",
						"&eClick to view listings"
				));
		getInventory().setItem(11, listingsBuilder.getItemStack());

		AItemStackBuilder createBuilder = new AItemStackBuilder(Material.GOLD_BARDING)
				.setName("&eCreate new Listing")
				.setLore(new ALoreBuilder(
						"&7Create a new listing",
						"&7on the player market.",
						"",
						"&eClick to view listings"
				));
		getInventory().setItem(15, createBuilder.getItemStack());
	}

	@Override
	public String getName() {
		return "Player Market";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(!event.getClickedInventory().equals(getInventory())) return;
		int slot = event.getSlot();

		if(slot == 11) {
			openPanel(((MarketGUI) gui).marketPanel);
		}

		if(slot == 15) {
			openPanel(((MarketGUI) gui).createListingPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
