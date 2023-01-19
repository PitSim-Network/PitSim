package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.misc.Sounds;
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
				.setName(canCreateListing() ? "&eCreate new Listing" : "&cCreate new Listing")
				.setLore(new ALoreBuilder(
						"&7Create a new listing",
						"&7on the player market.",
						"&7Active Listings: " + (canCreateListing() ? "&e" : "&c") + MarketManager.getActiveListings(player.getUniqueId()) + "&f/" + MarketManager.ListingLimit.getRank(player).limit,
						"",
						canCreateListing() ? "&eClick to view listings" : "&cCan't create more!"
				));
		getInventory().setItem(15, createBuilder.getItemStack());

		AItemStackBuilder yourBuilder = new AItemStackBuilder(Material.INK_SACK, 1, 7)
				.setName("&eYour Listings")
				.setLore(new ALoreBuilder(
						"&7View all of your",
						"&7current listings and",
						"&7claim any items or souls",
						"&7from ended listings.", "",
						"&eClick to view listings"
				));
		getInventory().setItem(13, yourBuilder.getItemStack());

	}

	public boolean canCreateListing() {
		return MarketManager.getActiveListings(player.getUniqueId()).size() < MarketManager.ListingLimit.getRank(player).limit;
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
			if(!canCreateListing()) {
				AOutput.error(player, "&cGet a greater Listing limit with a rank from &f&nstore.pitsim.net");
				Sounds.NO.play(player);
			}
			openPanel(((MarketGUI) gui).createListingPanel);
		}

		if(slot == 13) {
			openPanel(((MarketGUI) gui).yourListingsPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
