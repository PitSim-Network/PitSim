package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class MarketGUI extends AGUI {
	MarketPanel marketPanel;

	public MarketGUI(Player player) {
		super(player);

		marketPanel = new MarketPanel(this);
		setHomePanel(marketPanel);
	}

}
