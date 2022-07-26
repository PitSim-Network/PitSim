package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.TaintedManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShredJewelPanel extends AGUIPanel {

	public Map<Integer, Integer> slots = new HashMap<>();
	public TaintedGUI taintedGUI;

	public static Map<Player, ItemStack> shredMap = new HashMap<>();

	public ShredJewelPanel(AGUI gui) {
		super(gui);
		taintedGUI = (TaintedGUI) gui;

	}

	@Override
	public String getName() {
		return "Choose an Item to Shred";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 31) {
				openPanel(taintedGUI.getHomePanel());
			}

			if(!slots.containsKey(slot)) return;
			int invSlot = slots.get(slot);

			for(int i = 0; i < player.getInventory().getSize(); i++) {
				if(i == invSlot) {
					ItemStack item = player.getInventory().getItem(i);
					shredMap.put(player, item);
					openPanel(taintedGUI.shredConfirmPanel);
//					NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
//					nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) + 1);
//					EnchantManager.setItemLore(nbtItem.getItem(), player);
//					player.getInventory().setItem(i, nbtItem.getItem());
				}
			}

		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		placeItems();
	}

	public void placeItems() {
		updateInventory();
		int slot = 0;

		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(Misc.isAirOrNull(item)) continue;
			item = item.clone();

			ItemMeta ogMeta = item.getItemMeta();
			ogMeta.setLore(TaintedManager.descramble(ogMeta.getLore()));
			item.setItemMeta(ogMeta);

			NBTItem nbtItem = new NBTItem(item);
			if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) {
				ItemMeta meta = nbtItem.getItem().getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				List<String> lore = meta.getLore();
				lore.add("");
				lore.add(ChatColor.RED + "Click to Shred for " + ChatColor.WHITE + "1-10 Souls" + ChatColor.RED + ".");
				meta.setLore(lore);
				nbtItem.getItem().setItemMeta(meta);
				getInventory().setItem(slot, nbtItem.getItem());
				slots.put(slot, i);
				slot++;
			}

			if(nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef())) {
				ItemMeta meta = nbtItem.getItem().getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				List<String> lore = meta.getLore();
				lore.add("");
				lore.add(ChatColor.RED + "Click to Shred for " + ChatColor.WHITE + "20-30 Souls" + ChatColor.RED + ".");
				meta.setLore(lore);
				nbtItem.getItem().setItemMeta(meta);
				getInventory().setItem(slot, nbtItem.getItem());
				slots.put(slot, i);
				slot++;
			}
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Tainted Menu");
		meta.setLore(lore);
		back.setItemMeta(meta);

		getInventory().setItem(31, back);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}

}
