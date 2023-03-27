package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarketGUI extends AGUI {

	public static final Map<UUID, MarketGUI> guiMap = new HashMap<>();

	MarketSelectionPanel selectionPanel;
	MarketPanel marketPanel;
	CreateListingPanel createListingPanel;
	ListingInspectPanel listingInspectPanel;
	YourListingsPanel yourListingsPanel;

	public MarketGUI(Player player) {
		super(player);

		selectionPanel = new MarketSelectionPanel(this);
		marketPanel = new MarketPanel(this);
		createListingPanel = new CreateListingPanel(this);
		yourListingsPanel = new YourListingsPanel(this);
		setHomePanel(selectionPanel);

		guiMap.put(player.getUniqueId(), this);
	}

	public static void updateGUIS() {
		for(MarketGUI gui : guiMap.values()) {
			gui.marketPanel.createInventory();
			if(gui.listingInspectPanel != null) gui.listingInspectPanel.calculateItems();
			gui.yourListingsPanel.placeClaimables();
			gui.yourListingsPanel.placeListings();
		}
	}

}
