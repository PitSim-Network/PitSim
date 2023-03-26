package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class TaintedShopPanel extends AGUIPanel {
	public TaintedShopPanel(AGUI gui) {
		super(gui);
	}

	@Override
	public String getName() {
		return "Tainted Shop";

//		AItemStackBuilder back = new AI
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
