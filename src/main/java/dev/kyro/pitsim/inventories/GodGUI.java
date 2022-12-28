package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class GodGUI extends AGUI {
	public GodPanel godPanel;

	public GodGUI(Player player) {
		super(player);

		godPanel = new GodPanel(this);
		setHomePanel(godPanel);
	}
}
