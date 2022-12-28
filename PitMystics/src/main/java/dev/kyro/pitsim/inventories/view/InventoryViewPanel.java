package dev.kyro.pitsim.inventories.view;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryViewPanel extends AGUIPanel {
	public ViewGUI viewGUI;

	public InventoryViewPanel(AGUI gui) {
		super(gui);
		viewGUI = (ViewGUI) gui;

		Player target = viewGUI.target;
		for(int i = 0; i < 36; i++) {
			ItemStack itemStack = target.getInventory().getItem(i);
			getInventory().setItem(i, itemStack);
		}
	}

	@Override
	public String getName() {
		return ((ViewGUI) gui).target.getName() + "'s Inventory";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
