package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class CraftTaintedPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public TaintedGUI taintedGUI;

	public CraftTaintedPanel(AGUI gui) {
		super(gui);
		taintedGUI = (TaintedGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Craft an Item";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 22) {
				openPreviousGUI();
			}
			if(slot == 11) {
				if(canBuy(player, 100)) {
					AUtil.giveItemSafely(player, FreshCommand.getFreshItem(MysticType.TAINTED_SCYTHE, PantColor.RED));
					PitPlayer.getPitPlayer(player).taintedSouls -= 100;
					AOutput.send(player, "&a&lCRAFT!&7 Crafted &5Fresh Tainted Scythe&7.");
					Sounds.TAINTED_CRAFT.play(player);
					openPreviousGUI();
				} else {
					Sounds.NO.play(player);
					AOutput.send(player, "&c&lERROR!&7 Not enough souls!");
				}
			}
			if(slot == 15) {
				if(canBuy(player, 100)) {
					AUtil.giveItemSafely(player, FreshCommand.getFreshItem(MysticType.TAINTED_CHESTPLATE, PantColor.RED));
					PitPlayer.getPitPlayer(player).taintedSouls -= 100;
					AOutput.send(player, "&a&lCRAFT!&7 Crafted &5Fresh Tainted Chestplate&7.");
					Sounds.TAINTED_CRAFT.play(player);
					openPreviousGUI();
				} else {
					Sounds.NO.play(player);
					AOutput.send(player, "&c&lERROR!&7 Not enough souls!");
				}

			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		ItemStack scythe = new ItemStack(Material.GOLD_HOE);
		ItemMeta scytheMeta = scythe.getItemMeta();
		scytheMeta.setDisplayName(ChatColor.YELLOW + "Craft Fresh Tainted Scythe");
		scytheMeta.addEnchant(Enchantment.DURABILITY, 1, false);
		scytheMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> scytheLore = new ArrayList<>();
		scytheLore.add(ChatColor.GRAY + "The main weapon of the " + ChatColor.DARK_PURPLE + "Darkzone" + ChatColor.GRAY + ".");
		scytheLore.add(ChatColor.GRAY + "Enchantable with a " + ChatColor.LIGHT_PURPLE + "SPELL! " + ChatColor.GRAY + "enchant");
		scytheLore.add(ChatColor.GRAY + "and up to " + ChatColor.WHITE + "2 " + ChatColor.GRAY + "regular enchants.");
		scytheLore.add("");
		scytheLore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + "100 Tainted Souls");
		scytheLore.add("");
		if(canBuy(player, 100)) scytheLore.add(ChatColor.GREEN + "Click to Craft!");
		else scytheLore.add(ChatColor.RED + "Not enough Souls!");
		scytheMeta.setLore(scytheLore);
		scythe.setItemMeta(scytheMeta);
		getInventory().setItem(11, scythe);

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		chestMeta.setDisplayName(ChatColor.DARK_PURPLE + "Craft Fresh Tainted Chestplate");
		chestMeta.setColor(Color.fromRGB(PantColor.TAINTED.hexColor));
		List<String> chestLore = new ArrayList<>();
		chestLore.add(ChatColor.GRAY + "The main defense of the " + ChatColor.DARK_PURPLE + "Darkzone" + ChatColor.GRAY + ".");
		chestLore.add(ChatColor.GRAY + "Enchantable with an " + ChatColor.LIGHT_PURPLE + "EFFECT! " + ChatColor.GRAY + "enchant");
		chestLore.add(ChatColor.GRAY + "and up to " + ChatColor.WHITE + "2 " + ChatColor.GRAY + "regular enchants.");
		chestLore.add("");
		chestLore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + "100 Tainted Souls");
		chestLore.add("");
		if(canBuy(player, 100)) chestLore.add(ChatColor.GREEN + "Click to Craft!");
		else chestLore.add(ChatColor.RED + "Not enough Souls!");
		chestMeta.setLore(chestLore);
		chestplate.setItemMeta(chestMeta);
		getInventory().setItem(15, chestplate);

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Tainted Menu");
		meta.setLore(lore);
		back.setItemMeta(meta);

		getInventory().setItem(22, back);
	}

	public static boolean canBuy(Player player, int souls) {
		int playerSouls = PitPlayer.getPitPlayer(player).taintedSouls;
		if(playerSouls >= souls) return true;
		else return false;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
