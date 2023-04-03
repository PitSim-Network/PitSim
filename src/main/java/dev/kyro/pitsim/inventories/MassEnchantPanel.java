package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.TaintedEnchanting;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MassEnchantPanel extends AGUIPanel {

	public int tier = 0;
	String mysticType;
	ItemStack freshItem;

	public MassEnchantPanel(AGUI gui) {
		super(gui);
		cancelClicks = false;

		this.mysticType = ((MassEnchantGUI) gui).mysticType;

		freshItem = MysticFactory.getFreshItem(player, mysticType);
		for(int i = 0; i < 36; i++) {
			getInventory().setItem(i, freshItem.clone());
		}

		for(int i = 36; i < 45; i++) {
			AItemStackBuilder glass = new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15)
					.setName(" ");
			getInventory().setItem(i, glass.getItemStack());
		}

		setButtons();
	}

	public void setButtons() {
		AItemStackBuilder tierUp = new AItemStackBuilder(Material.STAINED_CLAY, 1, tier < 4 ? 5 : 14)
				.setName((tier < 4 ? "&a" : "&c") + "Tier Up")
				.setLore(new ALoreBuilder(
								"&7Click to tier up items",
								"",
								tier < 4 ? "&aClick to tier up!" : "&cItems are max tier!"
						)
				);
		getInventory().setItem(48, tierUp.getItemStack());

		AItemStackBuilder reset = new AItemStackBuilder(Material.STAINED_CLAY, 1, 4)
				.setName("&eReset Items")
				.setLore(new ALoreBuilder(
								"&7Click to reset items to fresh",
								"",
								"&eClick to reset!"
						)
				);
		getInventory().setItem(50, reset.getItemStack());
	}

	@Override
	public String getName() {
		return "Enchant Test";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(!event.getClickedInventory().getHolder().equals(this)) return;
		int slot = event.getSlot();

		if(slot >= 36) event.setCancelled(true);

		if(slot == 50) {
			for(int i = 0; i < 36; i++) {
				getInventory().setItem(i, freshItem.clone());
				tier = 0;
				setButtons();
			}
		}

		if(slot != 48) return;

		if(tier >= 4) {
			Sounds.ERROR.play(player);
			return;
		}

		for(int i = 0; i < 36; i++) {
			ItemStack item = getInventory().getItem(i);
			if(item == null) continue;

			ItemStack newItem = TaintedEnchanting.enchantItem(getInventory().getItem(i), player);

			if(newItem == null) return;

			ItemMeta meta = newItem.getItemMeta();
			meta.setDisplayName(EnchantManager.getMysticName(newItem));
			newItem.setItemMeta(meta);
			EnchantManager.setItemLore(newItem, player);

			getInventory().setItem(i, newItem);
			updateInventory();
		}

		tier++;
		updateInventory();
		setButtons();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
