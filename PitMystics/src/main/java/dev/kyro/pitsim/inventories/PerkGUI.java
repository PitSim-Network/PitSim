package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AInventoryBuilder;
import dev.kyro.arcticapi.gui.AInventoryGUI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PitPerk;
import dev.kyro.pitsim.controllers.PitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PerkGUI extends AInventoryGUI {

	public AInventoryBuilder builder;
	public Player player;
	public boolean inSubGUI = false;

	public PerkGUI(Player player) {
		super("Enchant GUI", 6);
		this.player = player;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(int i = 0; i < pitPlayer.pitPerks.length; i++) {
			PitPerk pitPerk = pitPlayer.pitPerks[i];
//			baseGUI.setItem(10 + i * 2, pitPerk.);
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		if(event.getClickedInventory().getHolder() == this) {


		} else {

		}
		updateGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		inSubGUI = false;
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
			player.getOpenInventory().setItem(i, baseGUI.getItem(i));
		}
	}
}
