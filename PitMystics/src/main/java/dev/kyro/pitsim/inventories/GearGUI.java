package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class GearGUI extends AGUI {

	public GearPanel gearPanel;

	public GearGUI(Player player) {
		super(player);

		gearPanel = new GearPanel(this);
		setHomePanel(gearPanel);

	}

}
