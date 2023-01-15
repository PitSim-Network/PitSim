package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class MarketGUI extends AGUI {
	MarketSelectionPanel selectionPanel;
	MarketPanel marketPanel;
	CreateListingPanel createListingPanel;

	public MarketGUI(Player player) {
		super(player);

		selectionPanel = new MarketSelectionPanel(this);
		marketPanel = new MarketPanel(this);
		createListingPanel = new CreateListingPanel(this);
		setHomePanel(selectionPanel);
	}

}
