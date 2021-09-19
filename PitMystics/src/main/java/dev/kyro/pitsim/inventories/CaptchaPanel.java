package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PlayerManager;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CaptchaPanel extends AGUIPanel {
	public CaptchaGUI captchaGUI;

	public int slot;
	public boolean completed = false;

	public CaptchaPanel(AGUI gui) {
		super(gui);
		captchaGUI = (CaptchaGUI) gui;

		slot = (int) (Math.random() * 27);
		for(int i = 0; i < 27; i++) {
			getInventory().setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}
		getInventory().setItem(slot, new ItemStack(Material.EYE_OF_ENDER));
	}

	@Override
	public String getName() {
		return "Click the thing that stands out";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == this.slot) {
				completed = true;
				player.closeInventory();
				PlayerManager.passedCaptcha.add(player.getUniqueId());
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(completed) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				new CaptchaGUI(player).open();
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}
}
