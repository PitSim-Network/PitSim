package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class LeggingsGUI extends AGUI {

	public LeggingsPanel leggingsPanel;

	public LeggingsGUI(Player player) {
		super(player);

		leggingsPanel = new LeggingsPanel(this);
		setHomePanel(leggingsPanel);

	}

}
