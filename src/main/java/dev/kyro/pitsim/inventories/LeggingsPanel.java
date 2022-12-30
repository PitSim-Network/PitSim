package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class LeggingsPanel extends AGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public LeggingsGUI leggingsGUI;

	public LeggingsPanel(AGUI gui) {
		super(gui);
		leggingsGUI = (LeggingsGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Purchase Leggings";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 13) {
				if(pitPlayer.gold < 5_000) {
					AOutput.error(player, "&cYou cannot afford this!");
					Sounds.NO.play(player);
					return;
				}

				pitPlayer.gold -= 5_000;
				AUtil.giveItemSafely(player, new ItemStack(Material.DIAMOND_LEGGINGS));
				Sounds.RENOWN_SHOP_PURCHASE.play(player);
				AOutput.send(player, "&a&lPURCHASE! &bDiamond Leggings");
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		AItemStackBuilder leggings = new AItemStackBuilder(Material.DIAMOND_LEGGINGS);
		leggings.setName("&bDiamond Leggings");
		ALoreBuilder leggingsLore = new ALoreBuilder();
		leggingsLore.addLore("&7Stronger Leggings to reduce", "&7Damage while in the &5Darkzone&7.", "",
				"&7Cost: &65000g", "", pitPlayer.gold < 5_000 ? "&cNot enough Gold!" : "&eClick to purchase");
		leggings.setLore(leggingsLore);
		getInventory().setItem(13, leggings.getItemStack());
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
