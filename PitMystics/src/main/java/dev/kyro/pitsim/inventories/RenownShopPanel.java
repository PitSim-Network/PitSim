package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.RenownUpgrade;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.RenownUpgradeDisplays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenownShopPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    public RenownShopGUI renownShopGUI;
    public RenownShopPanel(AGUI gui) {
        super(gui);
        renownShopGUI = (RenownShopGUI) gui;

    }

    @Override
    public String getName() {
        return "Renown Shop";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {

            for(RenownUpgrade upgrade : RenownUpgrade.values()) {
                if(slot == upgrade.slot) {
                    if(upgrade.isTiered) {
                        if(RenownUpgrade.getTier(player, upgrade) < upgrade.maxTiers) {
                            RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                            openPanel(renownShopGUI.renownShopConfirmPanel);
                        } else {
                            AOutput.error(player, "&aYou already unlocked the last upgrade!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        }
                    } else if(!RenownUpgrade.hasUpgrade(player, upgrade)) {
                        RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                        openPanel(renownShopGUI.renownShopConfirmPanel);
                    } else {
                        AOutput.error(player, "&aYou already unlocked this upgrade!");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                    }

                }
            }
            APlayerData.savePlayerData(player);
            updateInventory();
            for (RenownUpgrade upg : RenownUpgrade.values()) { getInventory().setItem(upg.slot, RenownUpgradeDisplays.getDisplayItem(upg, player)); }
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        for (RenownUpgrade upg : RenownUpgrade.values()) {
            getInventory().setItem(upg.slot, RenownUpgradeDisplays.getDisplayItem(upg, player));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
