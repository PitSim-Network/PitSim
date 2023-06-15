package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TaintedShopGUI extends AGUI {

	ShopHomePanel homePanel;
	ShredPanel shredPanel;
	TaintedShopPanel shopPanel;

	public TaintedShopGUI(Player player) {
		super(player);

		homePanel = new ShopHomePanel(this);
		shredPanel = new ShredPanel(this);
		shopPanel = new TaintedShopPanel(this);

		setHomePanel(homePanel);
	}
}
