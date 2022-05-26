package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.milkbowl.vault.chat.Chat;
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
		scytheLore.add(ChatColor.GRAY + "Enchantable with a " + ChatColor.LIGHT_PURPLE + "SPELL! " + ChatColor.GRAY + "enchant and");
		scytheLore.add(ChatColor.GRAY + "up to " + ChatColor.WHITE + "2 " + ChatColor.GRAY + "regular enchants.");
		scytheLore.add("");
		scytheLore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + "100 Tainted Souls");
		scytheLore.add("");
		if(canBuy(player, 100)) scytheLore.add(ChatColor.GREEN + "Click to Craft!");
		else scytheLore.add(ChatColor.RED + "Not enough Souls!");
		scytheMeta.setLore(scytheLore);
		scythe.setItemMeta(scytheMeta);
		getInventory().setItem(11, scythe);

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
