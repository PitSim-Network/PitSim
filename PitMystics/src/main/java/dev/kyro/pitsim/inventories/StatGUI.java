package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class StatGUI extends AGUI {
	public StatPanel statPanel;

	public StatGUI(Player player) {
		super(player);

		statPanel = new StatPanel(this);
		setHomePanel(statPanel);
	}
}