package net.pitsim.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.controllers.UpgradeManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.controllers.objects.RenownUpgrade;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class RenownShopConfirmPanel extends AGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public RenownShopGUI renownShopGUI;

	public RenownShopConfirmPanel(AGUI gui) {
		super(gui);
		renownShopGUI = (RenownShopGUI) gui;
	}

	@Override
	public String getName() {
		return "&eAre you sure?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 11) {
			RenownUpgrade upgrade = RenownShopGUI.purchaseConfirmations.get(player);

			if(upgrade.isTiered()) {
				int tier = UpgradeManager.getTier(player, upgrade);
				pitPlayer.renownUpgrades.put(upgrade.refName, tier + 1);
				pitPlayer.renown = pitPlayer.renown - upgrade.getTierCosts().get(tier);
			} else {
				pitPlayer.renownUpgrades.put(upgrade.refName, 1);
				pitPlayer.renown = pitPlayer.renown - upgrade.getUnlockCost();
			}

			if(upgrade.isTiered()) {
				AOutput.send(player, "&a&lPURCHASE! &6" + upgrade.name + " " + AUtil.toRoman(UpgradeManager.getTier(player, upgrade)));
			} else {
				AOutput.send(player, "&a&lPURCHASE! &6" + upgrade.name);
			}
			Sounds.RENOWN_SHOP_PURCHASE.play(player);

			RenownShopGUI.purchaseConfirmations.remove(player);
			openPanel(renownShopGUI.getHomePanel());
			updateInventory();
		} else if(slot == 15) {
			RenownShopGUI.purchaseConfirmations.remove(player);
			openPanel(renownShopGUI.getHomePanel());
			updateInventory();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		RenownUpgrade upgrade = RenownShopGUI.purchaseConfirmations.get(player);
		PitLoreBuilder loreBuilder = new PitLoreBuilder();
		String tieredString = upgrade.isTiered() ? " " + AUtil.toRoman(UpgradeManager.getTier(player, upgrade) + 1) : "";
		loreBuilder.addLongLine("&7Purchasing: &6" + upgrade.name + tieredString);
		loreBuilder.addLongLine("&7Cost: &e" + UpgradeManager.getNextCost(player, upgrade));
		ItemStack confirmStack = new AItemStackBuilder(Material.STAINED_CLAY, 1, (short) 13)
				.setName("&aConfirm")
				.setLore(loreBuilder)
				.getItemStack();
		getInventory().setItem(11, confirmStack);

		ItemStack cancelStack = new AItemStackBuilder(Material.STAINED_CLAY, 1, (short) 14)
				.setName("&cCancel")
				.setLore(new ALoreBuilder(
						"&7Return to previous menu"
				)).getItemStack();
		ItemStack cancel = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		ItemMeta cancelMeta = cancel.getItemMeta();
		cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
		cancelMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Return to previous menu."));
		cancel.setItemMeta(cancelMeta);

		getInventory().setItem(15, cancel);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		RenownShopGUI.purchaseConfirmations.remove(player);
	}
}
