package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MainProgressionPanel extends AGUIPanel {

	public MainProgressionPanel(AGUI gui) {
		super(gui);

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 45, 46, 47, 48, 49, 50, 51, 52);

		getInventory().setItem(53, HelpItemStacks.getMainProgressionStack());
	}

	@Override
	public String getName() {
		return "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Skill Tree";
	}

	@Override
	public int getRows() {
		return 6;
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
