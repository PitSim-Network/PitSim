package net.pitsim.spigot.inventories.stats;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class StatMenuPanel extends AGUIPanel {
	public StatGUI statGUI;

	public StatMenuPanel(AGUI gui) {
		super(gui);
		this.statGUI = (StatGUI) gui;

		ItemStack personalStack = new AItemStackBuilder(Material.DIAMOND)
				.setName("&bPersonal Statistics")
				.setLore(new ALoreBuilder(

				))
				.getItemStack();
		getInventory().setItem(11, personalStack);

		ItemStack leaderboardStack = new AItemStackBuilder(Material.DIAMOND_BLOCK)
				.setName("&bLeaderboards")
				.setLore(new ALoreBuilder(

				))
				.getItemStack();
		getInventory().setItem(15, leaderboardStack);

		updateInventory();
	}

	@Override
	public String getName() {
		return "Leaderboards and Statistics";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();
		if(slot == 11) {
			openPanel(statGUI.statPanel);
		} else if(slot == 15) {
			openPanel(statGUI.leaderboardPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
