package net.pitsim.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class KeeperGUI extends AGUI {

	public KeeperPanel keeperPanel;

	public KeeperGUI(Player player) {
		super(player);

		keeperPanel = new KeeperPanel(this);
		setHomePanel(keeperPanel);
	}
}
