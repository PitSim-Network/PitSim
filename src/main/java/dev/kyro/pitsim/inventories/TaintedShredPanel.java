package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.TaintedChestplate;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TaintedShredPanel extends AGUIPanel {
	public TaintedShredPanel(AGUI gui) {
		super(gui);
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		int slot = 9;

		for(int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack = player.getInventory().getItem(i);
			if(Misc.isAirOrNull(itemStack)) continue;
			itemStack = itemStack.clone();

			while(slot % 9 == 0 || slot % 9 == 8) slot++;

			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!MysticFactory.isJewel(itemStack, true) && !(pitItem instanceof TaintedScythe) &&
					!(pitItem instanceof TaintedChestplate)) continue;
			DarkzoneBalancing.ShredValue shredValue = DarkzoneBalancing.ShredValue.getShredValue(pitItem);
			if(shredValue == null) continue;

			List<String> lore = itemStack.getItemMeta().getLore();
			lore.add("");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to Shred for &f" +
					shredValue.getLowSouls() + "&7-&f" + shredValue.getHighSouls() + " Souls"));
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);

			NBTItem nbtItem = new NBTItem(itemStack, true);
			nbtItem.setInteger(NBTTag.INVENTORY_INDEX.getRef(), i);

			getInventory().setItem(slot, itemStack);
			slot++;
		}
	}

	@Override
	public String getName() {
		return "Shred Items";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		ItemStack clicked = event.getCurrentItem();
		if(Misc.isAirOrNull(clicked)) return;

		NBTItem nbtItem = new NBTItem(clicked, true);
		int inventoryIndex = nbtItem.getInteger(NBTTag.INVENTORY_INDEX.getRef());
		ItemStack realItem = player.getInventory().getItem(inventoryIndex).clone();

		DarkzoneBalancing.ShredValue shredValue = DarkzoneBalancing.ShredValue.getShredValue(ItemFactory.getItem(realItem));
		if(shredValue == null) return;

		openPanel(new ConfirmShredPanel(gui, realItem, shredValue));
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
