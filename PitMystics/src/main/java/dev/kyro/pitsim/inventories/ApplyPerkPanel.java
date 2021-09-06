package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.PerkEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class ApplyPerkPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PerkGUI perkGUI;

	public int perkNum;

	public ApplyPerkPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);

		for(PitPerk pitPerk : PerkManager.pitPerks) {

			if(pitPerk.renownUnlockable && !UpgradeManager.hasUpgrade(player, pitPerk.upgradeRef)) {
				ItemStack perkItem = new ItemStack(Material.BEDROCK);
				ItemMeta meta = perkItem.getItemMeta();

				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + pitPerk.name));

				List<String> lore = new ArrayList<>(pitPerk.getDescription());
				lore.add("");
				lore.add(ChatColor.RED + "Unlocked in the Renown shop!");
				meta.setLore(lore);
				perkItem.setItemMeta(meta);
				getInventory().setItem(pitPerk.guiSlot, perkItem);
			} else {

				ItemStack perkItem = new ItemStack(pitPerk.displayItem);
				ItemMeta meta = perkItem.getItemMeta();

				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + pitPerk.name));

				List<String> lore = new ArrayList<>(pitPerk.getDescription());
				lore.add("");

				if(perkGUI.isActive(pitPerk))
					lore.add(ChatColor.translateAlternateColorCodes('&', "&aAlready selected!"));
				else lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to select!"));

				if(pitPerk.name.equals("No Perk")) {
					meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + pitPerk.name));
					lore.clear();
					lore.add(ChatColor.GRAY + "Are you hardcore enough that you");
					lore.add(ChatColor.GRAY + "don't need any perk for this");
					lore.add(ChatColor.GRAY + "slot?");
					lore.add("");
					lore.add(ChatColor.YELLOW + "Click to remove perk!");
				}
				if(pitPlayer.hasPerk(pitPerk.INSTANCE)) {
					meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				meta.setLore(lore);
				perkItem.setItemMeta(meta);

				getInventory().setItem(pitPerk.guiSlot, perkItem);
			}
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Perks");
		meta.setLore(lore);
		back.setItemMeta(meta);

		getInventory().setItem(49, back);
	}

	@Override
	public String getName() {
		return "Choose a perk";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			for(PitPerk clickedPerk : PerkManager.pitPerks) {
				if(clickedPerk.guiSlot != slot) continue;

				if(clickedPerk.renownUnlockable && !UpgradeManager.hasUpgrade(player, clickedPerk.upgradeRef)) {
					AOutput.error(player, "&cThis perk needs to be unlocked in the renown shop!");
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
					return;
				}

				for(PitPerk activePerk : perkGUI.getActivePerks()) {
					if(activePerk != clickedPerk || activePerk.name.equals("No Perk")) continue;
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
					AOutput.error(perkGUI.player, "&cThat perk is already equipped");
					return;
				}

				PitPerk replacedPerk = perkGUI.getActivePerk(perkNum);
				PerkEquipEvent equipEvent = new PerkEquipEvent(clickedPerk, player, replacedPerk);
				Bukkit.getPluginManager().callEvent(equipEvent);


				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2F);
				perkGUI.setPerk(clickedPerk, perkNum);
				openPreviousGUI();
				return;
			}

			if(slot == 49) {
				openPreviousGUI();
				return;
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) { }

	@Override
	public void onClose(InventoryCloseEvent event) { }
}
