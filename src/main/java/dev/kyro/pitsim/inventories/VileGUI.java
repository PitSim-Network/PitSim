package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class VileGUI extends AGUI {

	public VilePanel vilePanel;

	public VileGUI(Player player) {
		super(player);

		vilePanel = new VilePanel(this);
		setHomePanel(vilePanel);
	}

}
