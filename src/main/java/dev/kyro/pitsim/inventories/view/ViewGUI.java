package dev.kyro.pitsim.inventories.view;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class ViewGUI extends AGUI {
	public Player target;

	public MainViewPanel mainViewPanel;
	public InventoryViewPanel inventoryViewPanel;

	public ViewGUI(Player player, Player target) {
		super(player);
		this.target = target;

		this.mainViewPanel = new MainViewPanel(this);
		this.inventoryViewPanel = new InventoryViewPanel(this);
		setHomePanel(mainViewPanel);
	}
}
