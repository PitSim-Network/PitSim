package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PerkPanel extends AGUIPanel {
	public PerkGUI perkGUI;

	public PerkPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Perk GUI";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 10 || slot == 12 || slot == 14 || slot == 16) {

				perkGUI.applyPerkPanel.perkNum = perkGUI.getPerkNum(slot);
				openPanel(perkGUI.applyPerkPanel);
				return;
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(int i = 0; i < pitPlayer.pitPerks.length; i++) {
			PitPerk pitPerk = pitPlayer.pitPerks[i];
			if(pitPerk == null) continue;

			ItemStack perkItem = new ItemStack(pitPerk.displayItem);
			ItemMeta meta = perkItem.getItemMeta();

			if(pitPerk.name.equals("No Perk")) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
						"&aPerk Slot #" + (i + 1)));
			} else {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
						"&ePerk Slot #" + (i + 1)));
			}
			List<String> lore = new ArrayList<>();

			if(pitPerk.name.equals("No Perk")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7Select a perk to fill this slot."));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7Selected: &a" + pitPerk.name));
				lore.add("");
				lore.addAll(pitPerk.getDescription());
			}
			lore.add("");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to choose perk!"));

			meta.setLore(lore);
			perkItem.setItemMeta(meta);


			getInventory().setItem(10 + i * 2, perkItem);
		}
		updateInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) { }
}
