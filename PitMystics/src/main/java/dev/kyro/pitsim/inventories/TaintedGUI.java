package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TaintedGUI extends AGUI {

	public TaintedPanel taintedPanel;
	public ShredJewelPanel shredJewelPanel;
	public ShredConfirmPanel shredConfirmPanel;
	public CraftTaintedPanel craftTaintedPanel;

	public TaintedGUI(Player player) {
		super(player);

		taintedPanel = new TaintedPanel(this);
		shredJewelPanel = new ShredJewelPanel(this);
		shredConfirmPanel = new ShredConfirmPanel(this);
		craftTaintedPanel = new CraftTaintedPanel(this);
		setHomePanel(taintedPanel);

	}

}
