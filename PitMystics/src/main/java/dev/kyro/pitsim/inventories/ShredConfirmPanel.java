package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShredConfirmPanel extends AGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public TaintedGUI taintedGUI;

	public ShredConfirmPanel(AGUI gui) {
		super(gui);
		taintedGUI = (TaintedGUI) gui;
	}


	@Override
	public String getName() {
		return "Are you sure?";
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
				player.getInventory().remove(ShredJewelPanel.shredMap.get(player));
				int souls = randNumber();
				PitPlayer.getPitPlayer(player).taintedSouls += souls;

				AOutput.send(player, "&5&lSHRED! &7You Shredded " +
						ShredJewelPanel.shredMap.get(player).getItemMeta().getDisplayName() + " &7for &f" + souls + " Tainted Souls&7.");
				ShredJewelPanel.shredMap.remove(player);
				Sounds.JEWEL_SHRED1.play(player);
				Sounds.JEWEL_SHRED2.play(player);

				if(TaintedPanel.hasJewels(player)) {
					taintedGUI.shredJewelPanel = new ShredJewelPanel(taintedGUI);
					openPanel(taintedGUI.shredJewelPanel);
				} else openPanel(taintedGUI.taintedPanel);

			}
			if(slot == 15) {
				ShredJewelPanel.shredMap.remove(player);
				openPanel(taintedGUI.getHomePanel());
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		ItemStack item = ShredJewelPanel.shredMap.get(player);
		if(item == null) {
			player.closeInventory();
			return;
		}

		ItemStack confirm = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
		ItemMeta confirmMeta = confirm.getItemMeta();
		List<String> confirmLore = new ArrayList<>();
		confirmMeta.setDisplayName(ChatColor.GREEN + "ARE YOU SURE?");
		confirmLore.add(ChatColor.translateAlternateColorCodes('&', "&7The Following Item will be permanently lost:"));
		confirmLore.add("");
		confirmLore.add(item.getItemMeta().getDisplayName());
		confirmLore.addAll(item.getItemMeta().getLore());
		confirmLore.add("");
		confirmLore.add(ChatColor.GREEN + "Click to Shred this Item!");
		confirmMeta.setLore(confirmLore);
		confirm.setItemMeta(confirmMeta);

		getInventory().setItem(11, confirm);


		ItemStack cancel = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		ItemMeta cancelMeta = confirm.getItemMeta();
		List<String> cancelLore = new ArrayList<>();
		cancelMeta.setDisplayName(ChatColor.RED + "CANCEL");
		cancelLore.add(ChatColor.GRAY + "Back to Tainted Menu");
		cancelMeta.setLore(cancelLore);
		cancel.setItemMeta(cancelMeta);

		getInventory().setItem(15, cancel);


	}

	public static int randNumber() {
		Random random = new Random();
		return random.nextInt(10 - 1 + 1) + 1;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
