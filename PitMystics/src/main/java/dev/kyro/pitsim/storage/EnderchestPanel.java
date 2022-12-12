package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnderchestPanel extends AGUIPanel {
	public EnderchestPanel(AGUI gui) {
		super(gui);
	}

	@Override
	public String getName() {
		return "Enderchest";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);

		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot < 9 || slot > 36) return;

		if((slot - 9) + 1 > rank.pages && slot < 27) {
			event.setCancelled(true);
			AOutput.error(player, "&cYou do not have permission to access this page!");
			AOutput.send(player, "&7Purchase more at &f&nhttps://store.pitsim.net");
			return;
		}

		StorageProfile profile = StorageManager.getProfile(player);
		if(!profile.hasData() || profile.isSaving()) return;

//		player.closeInventory();
		Inventory inventory = profile.getEnderchest(slot - 8);
		player.openInventory(inventory);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		EnderchestGUI.EnderchestPages rank = EnderchestGUI.EnderchestPages.getRank(player);

		for(int i = 0; i < 9; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 27; i < 36; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		}

		for(int i = 9; i < 27; i++) {
			ItemStack item = new ItemStack(Material.ENDER_CHEST);
			ItemMeta meta = item.getItemMeta();

			int page = (i - 9) + 1;
			meta.setDisplayName(ChatColor.DARK_PURPLE + "Enderchest Page " + (i - 8));
			List<String> lore = new ArrayList<>();
			lore.add("");

			if(!(page > rank.pages)) {
				lore.add(ChatColor.GREEN + "Unlocked!");
				meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			else {
				meta.setDisplayName(ChatColor.RED + "Enderchest Page " + (i - 8));
				lore.add(ChatColor.RED + "Locked!");
			}

			meta.setLore(lore);
			item.setItemMeta(meta);
			getInventory().setItem(i, item);
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
