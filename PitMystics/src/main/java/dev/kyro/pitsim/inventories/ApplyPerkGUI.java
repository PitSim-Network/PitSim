package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AInventoryBuilder;
import dev.kyro.arcticapi.gui.AInventoryGUI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.PitPerk;
import dev.kyro.pitsim.events.PerkEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ApplyPerkGUI extends AInventoryGUI {

	public AInventoryBuilder builder;
	public PerkGUI perkGUI;
	public int perkNum;

	public ApplyPerkGUI(PerkGUI perkGUI, int perkNum) {
		super("Choose a perk", 6);
		this.perkGUI = perkGUI;
		this.perkNum = perkNum;

		builder = new AInventoryBuilder(baseGUI)
				.createBorder(Material.STAINED_GLASS_PANE, 8);

		for(PitPerk pitPerk : PerkManager.pitPerks) {


			ItemStack perkItem = new ItemStack(pitPerk.displayItem);
			ItemMeta meta = perkItem.getItemMeta();

			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + pitPerk.name));
			List<String> lore = new ArrayList<>();

			lore.addAll(pitPerk.getDescription());
			lore.add("");

			if(isActive(pitPerk)) lore.add(ChatColor.translateAlternateColorCodes('&', "&aAlready selected!"));
			else lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to select!"));

			if(pitPerk.name.equals("No Perk")){
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + pitPerk.name));
				lore.clear();
				lore.add(ChatColor.GRAY + "Are you hardcore enough that you");
				lore.add(ChatColor.GRAY + "don't need any perk for this");
				lore.add(ChatColor.GRAY + "slot?");
				lore.add("");
				lore.add(ChatColor.YELLOW + "Click to remove perk!");
			}

			meta.setLore(lore);
			perkItem.setItemMeta(meta);

			baseGUI.setItem(pitPerk.guiSlot, perkItem);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Perks");
		meta.setLore(lore);
		back.setItemMeta(meta);

		baseGUI.setItem(49, back);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			for(PitPerk clickedPerk : PerkManager.pitPerks) {
				if(clickedPerk.guiSlot != slot) continue;

				for(PitPerk activePerk : perkGUI.getActivePerks()) {
					if(activePerk != clickedPerk || activePerk.name.equals("No Perk")) continue;
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
					AOutput.error(perkGUI.player, "That perk is already equipped");
					return;
				}

				PitPerk replacedPerk = perkGUI.getActivePerk(perkNum);
				PerkEquipEvent equipEvent = new PerkEquipEvent(clickedPerk, player, replacedPerk);
				Bukkit.getPluginManager().callEvent(equipEvent);


				player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2F);
				perkGUI.setPerk(clickedPerk, perkNum);
				perkGUI.player.openInventory(perkGUI.getInventory());
				perkGUI.updateGUI();
				return;
			}

			if(slot == 49) {
				perkGUI.player.openInventory(perkGUI.getInventory());
				perkGUI.updateGUI();
				return;
			}
		}
		updateGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		new BukkitRunnable() {
			@Override
			public void run() {
				updateGUI();
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void updateGUI() {

		for(int i = 0; i < baseGUI.getSize(); i++) {
			perkGUI.player.getOpenInventory().setItem(i, baseGUI.getItem(i));
		}
	}

	public boolean isActive(PitPerk pitPerk) {

		for(PitPerk activePerk : perkGUI.getActivePerks()) {

			if(activePerk == pitPerk) return true;
		}

		return false;
	}
}
