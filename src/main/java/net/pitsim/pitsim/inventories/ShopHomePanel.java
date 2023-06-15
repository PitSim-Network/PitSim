package net.pitsim.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ShopHomePanel extends AGUIPanel {
	public ShopHomePanel(AGUI gui) {
		super(gui);

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		getInventory().setItem(11, new AItemStackBuilder(Material.DOUBLE_PLANT)
				.setName("&eItem Shop")
				.setLore(new ALoreBuilder(
						"&7Buy items for &fSouls",
						"",
						"&eClick to open!"
				))
				.getItemStack());

		getInventory().setItem(15, new AItemStackBuilder(Material.GHAST_TEAR)
				.setName("&eShred Items")
				.setLore(new ALoreBuilder(
						"&7Shred items for &fSouls",
						"",
						"&eClick to open!"
				))
				.getItemStack());
	}

	@Override
	public String getName() {
		return "Buy and Shred Items";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		if(event.getSlot() == 11) {
			openPanel(((TaintedShopGUI) gui).shopPanel);
		} else if(event.getSlot() == 15) {
			openPanel(((TaintedShopGUI) gui).shredPanel);
		}

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
