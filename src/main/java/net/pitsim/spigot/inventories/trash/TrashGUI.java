package net.pitsim.spigot.inventories.trash;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TrashGUI extends AGUI {
	public TrashPanel trashPanel;

	public TrashGUI(Player player) {
		super(player);

		trashPanel = new TrashPanel(this);
		setHomePanel(trashPanel);
	}
}
