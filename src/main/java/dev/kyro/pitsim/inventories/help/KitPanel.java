package dev.kyro.pitsim.inventories.help;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.KitManager;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PlayerStats;
import dev.kyro.pitsim.exceptions.PitException;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class KitPanel extends AGUIPanel {
	public KitGUI kitGUI;

	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PlayerStats stats;

	public KitPanel(AGUI gui) {
		super(gui);
		this.kitGUI = (KitGUI) gui;
		this.stats = pitPlayer.stats;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);

		for(Kit kit : KitManager.kits) getInventory().setItem(kit.slot, kit.getDisplayStack());

		getInventory().setItem(26, HelpItemStacks.getKitsItemStack());

		updateInventory();
	}

	@Override
	public String getName() {
		return "Kits";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		int slot = event.getSlot();
		for(Kit kit : KitManager.kits) {
			if(kit.slot != slot) continue;
			try {
				kit.giveKit(player);
			} catch(PitException exception) {
				player.closeInventory();
				Sounds.NO.play(player);
				AOutput.error(player, "&c&lERROR!&7 Kit requires " + kit.items.size() + " open inventory slots!");
				return;
			}
			player.closeInventory();
			Sounds.SUCCESS.play(player);
			AOutput.send(player, "&a&lKIT!&7 Successfully received " + kit.items.size() + " items!");
			return;
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
