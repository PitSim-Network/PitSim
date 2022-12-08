package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnderchestPanel extends AGUIPanel {
	public EnderchestPanel(AGUI gui) {
		super(gui);
	}

	@Override
	public String getName() {
		return "Enderchest";
	}

	@Override
	public int getRows() {
		return 5;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot < 9 || slot > 36) return;

		StorageProfile profile = StorageManager.getProfile(player);
		if(!profile.hasData() || profile.isSaving()) return;

		Inventory inventory = profile.getEnderchest(slot - 9);
		player.openInventory(inventory);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		for(int i = 0; i < 9; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 36; i < 45; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 9; i < 27; i++) {
			ItemStack item = new ItemStack(Material.ENDER_CHEST);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("Page " + (i - 8));
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
