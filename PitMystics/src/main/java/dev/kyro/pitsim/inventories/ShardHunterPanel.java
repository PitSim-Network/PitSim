package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ShardHunterPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    RenownUpgrade upgrade = null;
    public RenownShopGUI renownShopGUI;
    public ShardHunterPanel(AGUI gui) {
        super(gui);
        renownShopGUI = (RenownShopGUI) gui;

    }

    @Override
    public String getName() {
        return "Shardhunter";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {
            assert upgrade != null;

            if(slot == 15) {
                    if(upgrade.levelReq > pitPlayer.playerLevel) {
                        AOutput.error(player, "&cYou are too low level to acquire this!");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        return;
                    }
                    if(upgrade.isTiered) {
                        if(upgrade.maxTiers != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                            return;
                        }
                        if(UpgradeManager.getTier(player, upgrade) < upgrade.maxTiers) {
                            RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                            openPanel(renownShopGUI.renownShopConfirmPanel);
                        } else {
                            AOutput.error(player, "&aYou already unlocked the last upgrade!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        }
                    } else if(!UpgradeManager.hasUpgrade(player, upgrade)) {
                        if(upgrade.renownCost > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                            return;
                        }
                        RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                        openPanel(renownShopGUI.renownShopConfirmPanel);
                    } else {
                        AOutput.error(player, "&aYou already unlocked this upgrade!");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                    }

            }
            APlayerData.savePlayerData(player);
            updateInventory();
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        for(RenownUpgrade renownUpgrade : UpgradeManager.upgrades) {
            if(renownUpgrade.refName.equals("SHARDHUNTER")) upgrade = renownUpgrade;
        }

        ItemStack gem = new ItemStack(Material.EMERALD);
        getInventory().setItem(8, gem);
        getInventory().setItem(15, upgrade.getDisplayItem(player, true));

    }


    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
