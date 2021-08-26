package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.RenownUpgrade;
import dev.kyro.pitsim.misc.RenownUpgradeDisplays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RenownShopConfirmPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    public RenownShopGUI renownShopGUI;
    public RenownShopConfirmPanel(AGUI gui) {
        super(gui);
        renownShopGUI = (RenownShopGUI) gui;

    }

    @Override
    public String getName() {
        return "Are you sure?";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 11) {
                RenownUpgrade upgrade = RenownShopGUI.purchaseConfirmations.get(player);
                if(upgrade.isTiered) {
                    if(playerData.contains(upgrade.name()))
                        playerData.set(upgrade.name(), RenownUpgrade.getTier(player, upgrade) + 1);
                        else playerData.set(upgrade.name(), 1);
                        pitPlayer.renown = pitPlayer.renown - upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade) - 1);
                } else {
                    playerData.set(upgrade.name(), 0);
                    pitPlayer.renown = pitPlayer.renown - upgrade.renownCost;
                }
                RenownShopGUI.purchaseConfirmations.remove(player);
                openPanel(renownShopGUI.getHomePanel());

                if(upgrade.isTiered) {
                    AOutput.send(player, ChatColor.translateAlternateColorCodes('&', "&a&lPURCHASE! &6" + upgrade.refName + " " + AUtil.toRoman(RenownUpgrade.getTier(player, upgrade))));
                } else {
                    AOutput.send(player, ChatColor.translateAlternateColorCodes('&', "&a&lPURCHASE! &6" + upgrade.refName));
                }
                ASound.play(player, Sound.ORB_PICKUP, 2, 1.5F);

            }

            if(slot == 15)  {
                RenownShopGUI.purchaseConfirmations.remove(player);
                openPanel(renownShopGUI.getHomePanel());
            }

            APlayerData.savePlayerData(player);
            updateInventory();
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ItemStack confirm = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        List<String> confirmLore = new ArrayList<>();
        RenownUpgrade upgrade = RenownShopGUI.purchaseConfirmations.get(player);
        if(upgrade.isTiered) {
            confirmLore.add(ChatColor.translateAlternateColorCodes('&', "&7Purchasing: &6" + upgrade.refName + " " +
                    AUtil.toRoman(RenownUpgrade.getTier(player, upgrade) + 1)));
            confirmLore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade))));
        } else {
            confirmLore.add(ChatColor.translateAlternateColorCodes('&', "&7Purchasing: &6" + upgrade.refName));
            confirmLore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.renownCost));
        }
        confirmMeta.setLore(confirmLore);
        confirm.setItemMeta(confirmMeta);

        getInventory().setItem(11, confirm);



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
