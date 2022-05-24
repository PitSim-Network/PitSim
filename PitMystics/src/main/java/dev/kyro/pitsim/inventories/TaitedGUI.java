package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TaitedGUI extends AGUI {

	public TaintedPanel taintedPanel;

	public TaitedGUI(Player player) {
		super(player);

		taintedPanel = new TaintedPanel(this);
		setHomePanel(taintedPanel);

	}

}
