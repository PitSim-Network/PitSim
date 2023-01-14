package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class MarketGUI extends AGUI {
	MarketSelectionPanel selectionPanel;
	MarketPanel marketPanel;

	public MarketGUI(Player player) {
		super(player);

		selectionPanel = new MarketSelectionPanel(this);
		marketPanel = new MarketPanel(this);
		setHomePanel(selectionPanel);
	}

}
