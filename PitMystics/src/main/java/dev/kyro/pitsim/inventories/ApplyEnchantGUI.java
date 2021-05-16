package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AInventoryGUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ApplyEnchantGUI extends AInventoryGUI {

	public MainEnchantGUI mainEnchantGUI;

	public ApplyEnchantGUI(MainEnchantGUI mainEnchantGUI) {
		super("Choose an Enchant", 6);
		this.mainEnchantGUI = mainEnchantGUI;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		if(event.getClickedInventory().getHolder() == this) {

		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void updateGUI() {


	}
}
