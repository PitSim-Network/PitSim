package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class AdminGUI extends AGUI {

	public AdminPanel adminPanel;
	public ServerViewPanel serverViewPanel;

	public AdminGUI(Player player) {
		super(player);

		adminPanel = new AdminPanel(this);
		setHomePanel(adminPanel);
	}
}
