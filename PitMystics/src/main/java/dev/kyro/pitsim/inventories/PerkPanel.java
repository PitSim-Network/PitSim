package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class  PerkPanel extends AGUIPanel {
	public PerkGUI perkGUI;

	public PerkPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Perks and Killstreaks";
	}

	@Override
	public int getRows() {
		return 5;
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

			if(slot == 34) {
				openPanel(perkGUI.megastreakPanel);
			}

			if(slot == 32) {
				PerkGUI.killstreakSlot = 3;
				openPanel(perkGUI.killstreakPanel);
			}

			if(slot == 30) {
				PerkGUI.killstreakSlot = 2;
				openPanel(perkGUI.killstreakPanel);
			}

			if(slot == 28) {
				PerkGUI.killstreakSlot = 1;
				openPanel(perkGUI.killstreakPanel);
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

		ItemStack killStreak = new ItemStack(Material.GOLD_BLOCK);
		ItemMeta ksMeta = killStreak.getItemMeta();
		ksMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eKillstreaks &c&lCOMING SOON"));
		killStreak.setItemMeta(ksMeta);

		getInventory().setItem(28, killStreak);
		getInventory().setItem(30, killStreak);
		getInventory().setItem(32, killStreak);

		ItemStack megaStreak = new ItemStack(pitPlayer.megastreak.guiItem().getType());
		ItemMeta msMeta = megaStreak.getItemMeta();
		msMeta.setDisplayName(ChatColor.YELLOW + "Megastreak");
		List<String> msLore = new ArrayList<>();
		if(pitPlayer.megastreak.getClass() != NoMegastreak.class) {
			msLore.add(ChatColor.GRAY + "Selected: " + ChatColor.GREEN + pitPlayer.megastreak.getRawName());
			msLore.add("");
			msLore.addAll(pitPlayer.megastreak.guiItem().getItemMeta().getLore());
		} else {
			msLore.add(ChatColor.GRAY + "Select a megastreak to fill this slot.");
		}
		msLore.add("");
		if(pitPlayer.megastreak.getClass() == NoMegastreak.class) {
			msLore.add(ChatColor.YELLOW + "Click to choose megastreak!");
		} else {
			msLore.add(ChatColor.YELLOW + "Click to switch megastreak!");
		}
		msMeta.setLore(msLore);
		megaStreak.setItemMeta(msMeta);

		getInventory().setItem(34, megaStreak);

		updateInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) { }
}
