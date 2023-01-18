package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class MarketGUI extends AGUI {
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
	}

}
