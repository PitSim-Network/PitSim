package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.TaintedChestplate;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaintedShredPanel extends APagedGUIPanel {
	public TaintedShredPanel(AGUI gui) {
		super(gui);

		addBackButton(getRows() * 9 - 5);
		buildInventory();
	}

	@Override
	public void addItems() {

		for(ItemStack invItem : player.getInventory()) {
			if(Misc.isAirOrNull(invItem)) continue;
			ItemStack itemStack = invItem.clone();

			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!MysticFactory.isJewel(itemStack, true) && !(pitItem instanceof TaintedScythe) &&
					!(pitItem instanceof TaintedChestplate)) continue;
			DarkzoneBalancing.ShredValue shredValue = DarkzoneBalancing.ShredValue.getShredValue(pitItem);
			if(shredValue == null) continue;

			ALoreBuilder lore = new ALoreBuilder(itemStack.getItemMeta().getLore());
			lore.addLore("",
					"&eClick to Shred for &f" + shredValue.getLowSouls() + "&7-&f" + shredValue.getHighSouls() + " Souls"
			);

			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setLore(lore.getLore());
			itemStack.setItemMeta(itemMeta);

			addItem(() -> itemStack, event -> openPanel(new ConfirmShredPanel(gui, itemStack, shredValue)));
		}
	}

	@Override
	public void setInventory() {
		super.setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public String getName() {
		return "Shred Items";
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
