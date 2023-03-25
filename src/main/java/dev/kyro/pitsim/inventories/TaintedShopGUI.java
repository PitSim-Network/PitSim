package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TaintedShopGUI extends AGUI {

	TaintedShredPanel shredPanel;

	public TaintedShopGUI(Player player) {
		super(player);

		shredPanel = new TaintedShredPanel(this);

		setHomePanel(shredPanel);
	}
}
