package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GearPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public GearGUI gearGUI;

	public GearPanel(AGUI gui) {
		super(gui);
		gearGUI = (GearGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Purchase Gear";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 12) {
				if(PitSim.VAULT.getBalance(player) < 5000) {
					AOutput.error(player, "&cYou cannot afford this!");
					Sounds.NO.play(player);
					return;
				}

				AItemStackBuilder leggings = new AItemStackBuilder(Material.DIAMOND_LEGGINGS);
				leggings.setName("&bDiamond Leggings");
				ALoreBuilder leggingsLore = new ALoreBuilder();
				leggingsLore.addLore("&7Stronger Leggings to reduce", "&7Damage while in the &5Darkzone");
				leggings.setLore(leggingsLore);

				PitSim.VAULT.withdrawPlayer(player, 5000);
				AUtil.giveItemSafely(player, leggings.getItemStack());
				Sounds.RENOWN_SHOP_PURCHASE.play(player);
				AOutput.send(player, "&a&lPURCHASE! &bDiamond Leggings");
			}

			if(slot == 14) {
				if(PitSim.VAULT.getBalance(player) < 5000) {
					AOutput.error(player, "&cYou cannot afford this!");
					Sounds.NO.play(player);
					return;
				}

				AItemStackBuilder sword = new AItemStackBuilder(Material.IRON_SWORD);
				sword.setName("&fIron Sword");
				ALoreBuilder swordLore = new ALoreBuilder();
				swordLore.addLore("&7A temporary item to use instead", "&7of a &5Tainted Scythe");
				sword.setLore(swordLore);

				PitSim.VAULT.withdrawPlayer(player, 5000);
				AUtil.giveItemSafely(player, sword.getItemStack());
				Sounds.RENOWN_SHOP_PURCHASE.play(player);
				AOutput.send(player, "&a&lPURCHASE! &7Iron Sword");
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		AItemStackBuilder leggings = new AItemStackBuilder(Material.DIAMOND_LEGGINGS);
		leggings.setName("&bDiamond Leggings");
		ALoreBuilder leggingsLore = new ALoreBuilder();
		leggingsLore.addLore("&7Stronger Leggings to reduce", "&7Damage while in the &5Darkzone&7.", "",
				"&7Cost: &65000g", "", PitSim.VAULT.getBalance(player) < 5000 ? "&cNot enough Gold!" : "&eClick to purchase");
		leggings.setLore(leggingsLore);
		getInventory().setItem(12, leggings.getItemStack());

		AItemStackBuilder sword = new AItemStackBuilder(Material.IRON_SWORD);
		sword.setName("&fIron Sword");
		ALoreBuilder swordLore = new ALoreBuilder();
		swordLore.addLore("&7A temporary item to use instead", "&7of a &5Tainted Scythe", "",
				"&7Cost: &65000g", "", PitSim.VAULT.getBalance(player) < 5000 ? "&cNot enough Gold!" : "&eClick to purchase");
		sword.setLore(swordLore);
		getInventory().setItem(14, sword.getItemStack());
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
