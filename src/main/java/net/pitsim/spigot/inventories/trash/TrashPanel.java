package net.pitsim.spigot.inventories.trash;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class TrashPanel extends AGUIPanel {
	public TrashPanel(AGUI gui) {
		super(gui);
		cancelClicks = false;
	}

	@Override
	public String getName() {
		return ChatColor.GRAY + "DELETES ITEMS AFTER CLOSING";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
