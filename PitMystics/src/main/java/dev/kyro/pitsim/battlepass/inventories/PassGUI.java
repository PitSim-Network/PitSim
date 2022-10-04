package dev.kyro.pitsim.battlepass.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class PassGUI extends AGUI {
	public PassPanel passPanel;

	public PassGUI(Player player) {
		super(player);

		passPanel = new PassPanel(this);
		setHomePanel(passPanel);
	}
}
