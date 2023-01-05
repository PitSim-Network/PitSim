package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class DonatorPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

	public DonatorGUI donatorGUI;

	public DonatorPanel(AGUI gui) {
		super(gui);
		donatorGUI = (DonatorGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Donator Perks";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 11) {
				if(!player.hasPermission("pitsim.chat")) return;
				openPanel(donatorGUI.chatOptionsPanel);
			} else if(slot == 12) {
				if(!player.hasPermission("pitsim.pantscolor")) return;
				openPanel(donatorGUI.pantsColorPanel);
			} else if(slot == 15) {
				if(!player.hasPermission("pitsim.itemrename")) return;
				ItemStack heldItem = player.getItemInHand();
				if(Misc.isAirOrNull(heldItem)) return;
				NBTItem nbtItem = new NBTItem(heldItem);
				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
					player.closeInventory();
					ItemRename.renameItem(player, player.getItemInHand());
				}
			} else if(slot == 16) {
				if(!player.hasPermission("pitsim.chatcolor")) return;
				openPanel(donatorGUI.chatColorPanel);
			}
		}

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta pantsmeta = pants.getItemMeta();
		List<String> pantslore = new ArrayList<>();
		pantslore.add(ChatColor.GRAY + "Choose from a selection of premium");
		pantslore.add(ChatColor.GRAY + "colors to dye your pants with");
		pantslore.add("");
		if(player.hasPermission("pitsim.pantscolor")) {
			pantslore.add(ChatColor.YELLOW + "Click to select!");
			pantsmeta.setDisplayName(ChatColor.YELLOW + "Pants Colorizer");
		} else {
			pantslore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &5Overpowered &crank!"));
			pantsmeta.setDisplayName(ChatColor.RED + "Pants Colorizer");
		}
		pantsmeta.setLore(pantslore);
		pants.setItemMeta(pantsmeta);
		pants = PantColor.setPantColor(pants, PantColor.HARVEST_RED);

		ItemStack rename = new ItemStack(Material.NAME_TAG);
		ItemMeta renamemeta = rename.getItemMeta();
		List<String> renamelore = new ArrayList<>();
		renamelore.add(ChatColor.GRAY + "Rename any mystic item that you're");
		renamelore.add(ChatColor.GRAY + "holding to anything you want");
		renamelore.add("");
		ItemStack heldItem = player.getItemInHand();
		if(player.hasPermission("pitsim.itemrename")) {
			if(!Misc.isAirOrNull(heldItem)) {
				NBTItem nbtItem = new NBTItem(heldItem);
				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
					renamelore.add(ChatColor.GRAY + "Holding: " + heldItem.getItemMeta().getDisplayName());
					renamelore.add("");
					renamelore.add(ChatColor.YELLOW + "Click to select!");
					renamemeta.setDisplayName(ChatColor.YELLOW + "Rename Item");
				} else {
					renamelore.add(ChatColor.GRAY + "Holding: " + ChatColor.RED + "Invalid Item!");
					renamelore.add("");
					renamelore.add(ChatColor.RED + "Must be holding a mystic item!");
					renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
				}
			} else {
				renamelore.add(ChatColor.GRAY + "Holding: " + ChatColor.RED + "Invalid Item!");
				renamelore.add("");
				renamelore.add(ChatColor.RED + "Must be holding a mystic item!");
				renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
			}
		} else {
			renamelore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &bMiraculous &crank!"));
			renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
		}
		renamemeta.setLore(renamelore);
		rename.setItemMeta(renamemeta);

		ItemStack chat = new ItemStack(Material.SIGN);
		ItemMeta chatmeta = chat.getItemMeta();
		List<String> chatlore = new ArrayList<>();
		chatlore.add(ChatColor.GRAY + "Choose any color to send your chat");
		chatlore.add(ChatColor.GRAY + "messages in");
		chatlore.add("");
		if(player.hasPermission("pitsim.chatcolor")) {
			if(ChatColorPanel.playerChatColors.containsKey(player)) {
				chatlore.add(ChatColor.GRAY + "Selected: " + ChatColorPanel.playerChatColors.get(player).chatColor + ChatColorPanel.playerChatColors.get(player).refName);
				chatlore.add("");
			}
			chatlore.add(ChatColor.YELLOW + "Click to select!");
			chatmeta.setDisplayName(ChatColor.YELLOW + "Chat Color");
		} else {
			chatlore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &6Unthinkable &crank!"));
			chatmeta.setDisplayName(ChatColor.RED + "Chat Color");
		}
		chatmeta.setLore(chatlore);
		chat.setItemMeta(chatmeta);

		ItemStack filter = new ItemStack(Material.REDSTONE_COMPARATOR);
		ItemMeta filtermeta = filter.getItemMeta();
		List<String> filterlore = new ArrayList<>();
		filterlore.add(ChatColor.GRAY + "Filter out certain in-game messages");
		filterlore.add(ChatColor.GRAY + "to better your game experience");
		filterlore.add("");
		if(player.hasPermission("pitsim.chat")) {
			filterlore.add(ChatColor.YELLOW + "Click to select!");
			filtermeta.setDisplayName(ChatColor.YELLOW + "Chat Options");
		} else {
			filterlore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &eLegendary &crank!"));
			filtermeta.setDisplayName(ChatColor.RED + "Chat Options");
		}
		filtermeta.setLore(filterlore);
		filter.setItemMeta(filtermeta);

		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta headmeta = (SkullMeta) head.getItemMeta();
		headmeta.setOwner(player.getDisplayName());
		headmeta.setDisplayName(ChatColor.YELLOW + "Rank Information");
		List<String> headlore = new ArrayList<>();
		String rank = "&7Current rank: %luckperms_groups%";
		headlore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, rank)));
		headlore.add("");
		if(player.hasPermission("reportex.use")) headlore.add(ChatColor.GREEN + "Access to /report");
		else headlore.add(ChatColor.RED + "Access to /report");
		if(player.hasPermission("pitsim.show")) headlore.add(ChatColor.GREEN + "Access to /show");
		else headlore.add(ChatColor.RED + "Access to /show");
		if(player.hasPermission("pitsim.chat")) headlore.add(ChatColor.GREEN + "Access to /chat");
		else headlore.add(ChatColor.RED + "Access to /chat");
		if(player.hasPermission("pitsim.pantscolor")) headlore.add(ChatColor.GREEN + "Pants Colorizer");
		else headlore.add(ChatColor.RED + "Pants Colorizer");
		if(player.hasPermission("pitsim.deathcry")) headlore.add(ChatColor.GREEN + "Death Cries");
		else headlore.add(ChatColor.RED + "Death Cries");
		if(player.hasPermission("pitsim.killeffect")) headlore.add(ChatColor.GREEN + "Kill Effects");
		else headlore.add(ChatColor.RED + "Kill Effects");
		if(player.hasPermission("pitsim.itemrename")) headlore.add(ChatColor.GREEN + "Item Rename");
		else headlore.add(ChatColor.RED + "Item Rename");
		if(player.hasPermission("pitsim.stereo")) headlore.add(ChatColor.GREEN + "Stereo Pants");
		else headlore.add(ChatColor.RED + "Stereo Pants");
		if(player.hasPermission("pitsim.chatcolor")) headlore.add(ChatColor.GREEN + "Chat Colors");
		else headlore.add(ChatColor.RED + "Chat Colors");
		if(player.hasPermission("galacticvaults.limit.14"))
			headlore.add(ChatColor.GRAY + "14x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		else if(player.hasPermission("galacticvaults.limit.10"))
			headlore.add(ChatColor.GRAY + "10x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		else if(player.hasPermission("galacticvaults.limit.7"))
			headlore.add(ChatColor.GRAY + "7x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		else if(player.hasPermission("galacticvaults.limit.5"))
			headlore.add(ChatColor.GRAY + "5x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		else if(player.hasPermission("galacticvaults.limit.3"))
			headlore.add(ChatColor.GRAY + "3x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		else if(player.hasPermission("galacticvaults.limit.1"))
			headlore.add(ChatColor.GRAY + "1x " + ChatColor.DARK_PURPLE + "Ender Chest Pages");
		headmeta.setLore(headlore);
		head.setItemMeta(headmeta);

		getInventory().setItem(10, head);
		getInventory().setItem(11, filter);
		getInventory().setItem(12, pants);
		getInventory().setItem(15, rename);
		getInventory().setItem(16, chat);

		updateInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
