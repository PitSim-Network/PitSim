package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.ProxyMessaging;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class BoosterPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public BoosterGUI boosterGUI;

	public BoosterPanel(AGUI gui) {
		super(gui);
		boosterGUI = (BoosterGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Boosters";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() != this) return;
		for(Booster booster : BoosterManager.boosterList) {
			if(booster.slot != slot) continue;
			if(Booster.getBoosterAmount(player, booster) < 1 || booster.minutes != 0) {
				Sounds.SUCCESS.play(player);
				AOutput.send(player, "&6&lSTORE!&7 Purchase boosters at &6&nhttps://store.pitsim.net");
			} else {
				Sounds.SUCCESS.play(player);
				Booster.setBooster(player, booster, Booster.getBoosterAmount(player, booster) - 1);
				ProxyMessaging.sendBoosterUse(booster, player, 60, true);
				LogManager.onBoosterActivate(player, booster);
			}

			player.closeInventory();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		for(Booster booster : BoosterManager.boosterList) {
			ItemStack itemStack = booster.getDisplayStack(player);
			getInventory().setItem(booster.slot, itemStack);
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
