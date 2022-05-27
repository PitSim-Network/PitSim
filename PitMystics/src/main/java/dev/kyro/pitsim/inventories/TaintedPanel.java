package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TaintedPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public TaintedGUI taintedGUI;

	public TaintedPanel(AGUI gui) {
		super(gui);
		taintedGUI = (TaintedGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Tainted Menu";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 15) {
				if(hasJewels(player)) openPanel(taintedGUI.shredJewelPanel);
				else Sounds.NO.play(player);
			}
			if(slot == 11) {
				openPanel(taintedGUI.craftTaintedPanel);
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		ItemStack soul = new ItemStack(Material.INK_SACK, 1, (short) 7);
		ItemMeta soulMeta = soul.getItemMeta();
		soulMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "TAINTED SOULS");
		List<String> soulLore = new ArrayList<>();
		soulLore.add(ChatColor.GRAY + "The lost souls of fallen warriors");
		soulLore.add("");
		soulLore.add(ChatColor.GRAY + "Obtained by:");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Killing Bosses");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Shredding " + ChatColor.DARK_AQUA + "JEWEL! " + ChatColor.LIGHT_PURPLE + "Items");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Opening " + ChatColor.DARK_PURPLE + " Tainted Crates");
		soulLore.add("");
		soulLore.add(ChatColor.GRAY + "You have " + ChatColor.GREEN + countSouls(player) + ChatColor.GRAY + " Souls");
		soulMeta.setLore(soulLore);
		soul.setItemMeta(soulMeta);
		getInventory().setItem(13, soul);

		ItemStack shred = new ItemStack(Material.GOLD_SWORD);
		ItemMeta shredMeta = shred.getItemMeta();
		shredMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
		shredMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		shredMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHRED JEWEL ITEMS");
		List<String> shredLore = new ArrayList<>();
		shredLore.add(ChatColor.GRAY + "Turn " + ChatColor.DARK_AQUA + "JEWEL! " + ChatColor.GRAY + "items into");
		shredLore.add(ChatColor.WHITE + "1-10 Tainted Souls" + ChatColor.GRAY + ".");
		shredLore.add("");
		if(hasJewels(player)) shredLore.add(ChatColor.GREEN + "Click to shred Jewels!");
		else shredLore.add(ChatColor.RED + "No Jewels available!");
		shredMeta.setLore(shredLore);
		shred.setItemMeta(shredMeta);
		getInventory().setItem(15, shred);

		ItemStack craft = new ItemStack(Material.WORKBENCH);
		ItemMeta craftMeta = craft.getItemMeta();
		craftMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "CRAFT TAINTED ITEMS");
		List<String> craftLore = new ArrayList<>();
		craftLore.add(ChatColor.GRAY + "Use " + ChatColor.WHITE + "Tainted Souls " + ChatColor.GRAY + "to craft");
		craftLore.add(ChatColor.DARK_PURPLE + "Fresh Tainted Scythes " + ChatColor.GRAY + "and");
		craftLore.add(ChatColor.DARK_PURPLE + "Fresh Tainted Chestplates");
		craftLore.add("");
		craftLore.add(ChatColor.GREEN + "Click to open Crafting Menu!");
		craftMeta.setLore(craftLore);
		craft.setItemMeta(craftMeta);
		getInventory().setItem(11, craft);

	}

	public static int countSouls(Player player) {
		return PitPlayer.getPitPlayer(player).taintedSouls;
	}

	public static boolean hasJewels(Player player) {
		for (ItemStack itemStack : player.getInventory()) {
			if(Misc.isAirOrNull(itemStack)) continue;

			NBTItem nbtItem = new NBTItem(itemStack);
			if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) return true;
		}
		return false;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
