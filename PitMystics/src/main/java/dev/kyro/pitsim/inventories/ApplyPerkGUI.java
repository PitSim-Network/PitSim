package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AInventoryGUI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.PitPerk;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ApplyPerkGUI extends AInventoryGUI {

	public PerkGUI perkGUI;
	public int perkNum;

	public ApplyPerkGUI(PerkGUI perkGUI, int perkNum) {
		super("Choose a perk", 6);
		this.perkGUI = perkGUI;
		this.perkNum = perkNum;

		for(PitPerk pitPerk : PerkManager.pitPerks) {

			baseGUI.setItem(pitPerk.guiSlot, pitPerk.getDisplayItem());
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			for(PitPerk clickedPerk : PerkManager.pitPerks) {
				if(clickedPerk.guiSlot != slot) continue;

				for(PitPerk activePerk : perkGUI.getActivePerks()) {
					if(activePerk != clickedPerk) continue;
					AOutput.error(perkGUI.player, "That perk is already equipped");
					return;
				}

				perkGUI.setPerk(clickedPerk, perkNum);
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
}
