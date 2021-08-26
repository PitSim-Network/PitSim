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

import java.util.*;

public class RenownShopPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
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
                    if(upgrade.levelReq > pitPlayer.playerLevel) {
                        AOutput.error(player, "&cYou are too low level to acquire this!");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        continue;
                    }
                    if(upgrade.isTiered) {
                        if(upgrade.maxTiers != RenownUpgrade.getTier(player, upgrade) && upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade)) > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                            continue;
                        }
                        if(RenownUpgrade.getTier(player, upgrade) < upgrade.maxTiers) {
                            RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                            openPanel(renownShopGUI.renownShopConfirmPanel);
                        } else {
                            AOutput.error(player, "&aYou already unlocked the last upgrade!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        }
                    } else if(!RenownUpgrade.hasUpgrade(player, upgrade)) {
                        if(upgrade.renownCost > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                            continue;
                        }
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
            refresh();
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        refresh();
    }

    public void refresh() {

        ItemStack item = new ItemStack(Material.BEDROCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Unknown upgrade");


        for (RenownUpgrade upg : RenownUpgrade.values()) {
            if(upg.levelReq > pitPlayer.playerLevel) {
                List<String> lore = Collections.singletonList(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + upg.levelReq);
                meta.setLore(lore);
                item.setItemMeta(meta);
                getInventory().setItem(upg.slot, item);
            } else getInventory().setItem(upg.slot, RenownUpgradeDisplays.getDisplayItem(upg, player));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
