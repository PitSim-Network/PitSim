package dev.kyro.pitsim;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;
import dev.kyro.pitsim.storage.EnderchestPanel;

public class EnderchestGUI extends AGUI {

	public EnderchestPanel panel;

	public EnderchestGUI(Player player) {
		super(player);

		this.panel = new EnderchestPanel(this);
		setHomePanel(panel);
	}
}
