package dev.kyro.pitsim.inventories.help;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class HelpGUI extends AGUI {
	public KitPanel kitPanel;

	public HelpGUI(Player player) {
		super(player);

		this.kitPanel = new KitPanel(this);
//		setHomePanel(kitPanel);
	}
}