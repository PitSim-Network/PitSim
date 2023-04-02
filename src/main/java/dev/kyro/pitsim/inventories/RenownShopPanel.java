package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIManager;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class RenownShopPanel extends APagedGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public RenownShopGUI renownShopGUI;

	public RenownShopPanel(AGUI gui) {
		super(gui);
		renownShopGUI = (RenownShopGUI) gui;
		addBackButton(addTaggedItem(getRows() * 9 - 5, AGUIManager::getBackItemStack, event -> new PrestigeGUI(player).open()));
		buildInventory();
	}

	@Override
	public void addItems() {
		for(RenownUpgrade upgrade : UpgradeManager.upgrades) addItem(() -> upgrade.getDisplayItem(player), event -> {
			boolean hasUpgrade = UpgradeManager.hasUpgrade(player, upgrade);
			boolean isTiered = upgrade.isTiered();
			boolean isMaxed = UpgradeManager.isMaxed(player, upgrade);
			int renownCost = isMaxed ? -1 : UpgradeManager.getNextCost(player, upgrade);
			AGUIPanel subPanel = renownShopGUI.getSubPanel(upgrade);

			if(upgrade.prestigeReq > pitPlayer.prestige) {
				AOutput.error(player, "&c&lERROR!&7 You need to have prestige &e" +
						AUtil.toRoman(upgrade.prestigeReq) + " &7to acquire this!");
				Sounds.NO.play(player);
				return;
			}

			if(hasUpgrade && subPanel != null) {
				openPanel(renownShopGUI.getSubPanel(upgrade));
				return;
			}

			if(isMaxed) {
				if(isTiered) {
					AOutput.error(player, "&a&lMAXED!&7 You already unlocked the last upgrade!");
				} else {
					AOutput.error(player, "&a&lMAXED!&7 You already unlocked this upgrade!");
				}
				Sounds.NO.play(player);
				return;
			}

			if(renownCost > pitPlayer.renown) {
				AOutput.error(player, "&c&lERROR!&7 You do not have enough renown!");
				Sounds.NO.play(player);
				return;
			}

			RenownShopGUI.purchaseConfirmations.put(player, upgrade);
			openPanel(renownShopGUI.renownShopConfirmPanel);
		});
	}

	@Override
	public void setInventory() {
		super.setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public String getName() {
		return "&eRenown Shop";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
