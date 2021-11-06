package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class  PerkPanel extends AGUIPanel {
	public List<Integer> killstreakLevels = Arrays.asList(0, 75, 90);
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
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
				if(pitPlayer.level < killstreakLevels.get(2)) {
					Sounds.NO.play(player);
					AOutput.error(player, "&cYou are too low level to use this slot!");
					return;
				}
				perkGUI.killstreakSlot = 3;
				openPanel(perkGUI.killstreakPanel);
			}

			if(slot == 30) {
				if(pitPlayer.level < killstreakLevels.get(1)) {
					Sounds.NO.play(player);
					AOutput.error(player, "&cYou are too low level to use this slot!");
					return;
				}
				perkGUI.killstreakSlot = 2;
				openPanel(perkGUI.killstreakPanel);
			}

			if(slot == 28) {
				if(pitPlayer.level < killstreakLevels.get(0)) {
					Sounds.NO.play(player);
					AOutput.error(player, "&cYou are too low level to use this slot!");
					return;
				}
				perkGUI.killstreakSlot = 1;
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

		for(int i = 0; i < pitPlayer.killstreaks.size(); i++) {
			if(pitPlayer.level < killstreakLevels.get(i)) {
				AItemStackBuilder lockedBuilder = new AItemStackBuilder(Material.BEDROCK);
				lockedBuilder.setLore(new ALoreBuilder("&7Required level: [" + PrestigeValues.
						getLevelColor(killstreakLevels.get(i)) + killstreakLevels.get(i) + "&7]"));
				lockedBuilder.setName("&cKillstreak slot #" + (i + 1));
				getInventory().setItem(28 + (2 * i), lockedBuilder.getItemStack());
				continue;
			}
			Killstreak killstreak = pitPlayer.killstreaks.get(i);
			AItemStackBuilder builder = new AItemStackBuilder(killstreak.getDisplayItem(player));
			ALoreBuilder loreBuilder = new ALoreBuilder();
			if(!killstreak.refName.equals("NoKillstreak")) {
				builder.setName("&eKillstreak slot #" + (i + 1));
				loreBuilder.addLore("&7Selected: &e" + killstreak.name, "");
				loreBuilder.addLore(builder.getItemStack().getItemMeta().getLore());
				loreBuilder.addLore("", "&eClick to switch killstreak!");

			} else {
				builder.setName("&aKillstreak slot #" + (i + 1));
				loreBuilder.addLore("&7Select a killstreak for this", "&7slot.", "", "&eClick to select killstreak!");
			}
			builder.setLore(loreBuilder);
			getInventory().setItem(28 + (2 * i), builder.getItemStack());

		}

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
