package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class PrestigeGUI extends AGUI {

	public PrestigePanel prestigePanel;
	public PrestigeConfirmPanel prestigeConfirmPanel;

	public PrestigeGUI(Player player) {
		super(player);

		prestigePanel = new PrestigePanel(this);
		prestigeConfirmPanel = new PrestigeConfirmPanel(this);
		setHomePanel(prestigePanel);
	}

}
