package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.ChunkOfVile;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VilePanel extends AGUIPanel {

	public Map<Integer, Integer> slots = new HashMap<>();
	public VileGUI vileGUI;

	public VilePanel(AGUI gui) {
		super(gui);
		vileGUI = (VileGUI) gui;

	}

	@Override
	public String getName() {
		return "Choose an item to repair";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		if(Misc.isAirOrNull(event.getCurrentItem())) {
			player.closeInventory();
			return;
		}

		ItemStack vileStack = player.getItemInHand();
		PitItem pitItem = ItemFactory.getItem(vileStack);
		if(!(pitItem instanceof ChunkOfVile)) {
			player.closeInventory();
			return;
		}

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(!slots.containsKey(slot)) return;
			int invSlot = slots.get(slot);

			for(int i = 0; i < player.getInventory().getSize(); i++) {
				if(i != invSlot) continue;
				NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
				nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) + 1);
				EnchantManager.setItemLore(nbtItem.getItem(), player);
				player.getInventory().setItem(i, nbtItem.getItem());
				player.closeInventory();

				TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&5WITHERCRAFT!&7 Repaired "));
				message.addExtra(Misc.createItemHover(nbtItem.getItem()));
				message.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7!")));
				player.sendMessage(message);

				LogManager.onItemRepair(player, nbtItem.getItem());

				Sounds.WITHERCRAFT_1.play(player);
				Sounds.WITHERCRAFT_2.play(player);

				if(vileStack.getAmount() == 1) {
					player.setItemInHand(new ItemStack(Material.AIR));
				} else {
					vileStack.setAmount(vileStack.getAmount() - 1);
					player.setItemInHand(vileStack);
				}
			}
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;

		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(item)) continue;

			NBTItem nbtItem = new NBTItem(item);
			if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) {
				if(nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()).equals(nbtItem.getInteger(NBTTag.MAX_LIVES.getRef())))
					continue;
				ItemMeta meta = nbtItem.getItem().getItemMeta();
				List<String> lore = meta.getLore();
				lore.add("");
				lore.add(ChatColor.YELLOW + "Click to repair!");
				meta.setLore(lore);
				nbtItem.getItem().setItemMeta(meta);
				getInventory().setItem(slot, nbtItem.getItem());
				slots.put(slot, i);
				slot++;
			}

		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}

}
