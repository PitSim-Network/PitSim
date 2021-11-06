package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return 6;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 49) {
                PrestigeGUI prestigeGUI = new PrestigeGUI(player);
                prestigeGUI.open();
            }

            for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
                if(slot == upgrade.guiSlot) {
                    if(upgrade.prestigeReq > pitPlayer.prestige) {
                        AOutput.error(player, "&cYou are too low level to acquire this!");
                        Sounds.NO.play(player);
                        continue;
                    }
                    if(upgrade.isTiered) {
                        if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.getCustomPanel() != null) {
                            openPanel(upgrade.getCustomPanel());
                            continue;
                        }
                        if(upgrade.maxTiers != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            Sounds.NO.play(player);
                            continue;
                        }
                        if(UpgradeManager.getTier(player, upgrade) < upgrade.maxTiers) {
                            RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                            openPanel(renownShopGUI.renownShopConfirmPanel);
                        } else {
                            AOutput.error(player, "&aYou already unlocked the last upgrade!");
                            Sounds.NO.play(player);
                        }
                    } else if(!UpgradeManager.hasUpgrade(player, upgrade)) {
                        if(upgrade.renownCost > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            Sounds.NO.play(player);
                            continue;
                        }
                        RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                        openPanel(renownShopGUI.renownShopConfirmPanel);
                    } else {
                        if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.getCustomPanel() != null) {
                            openPanel(upgrade.getCustomPanel());
                            continue;
                        }
                        AOutput.error(player, "&aYou already unlocked this upgrade!");
                        Sounds.NO.play(player);
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


        for (RenownUpgrade upg : UpgradeManager.upgrades) {
            if(upg.prestigeReq > pitPlayer.prestige) {
                List<String> lore = Collections.singletonList(ChatColor.GRAY + "Prestige: " + ChatColor.YELLOW + AUtil.toRoman(upg.prestigeReq));
                meta.setLore(lore);
                item.setItemMeta(meta);
                getInventory().setItem(upg.guiSlot, item);
            } else getInventory().setItem(upg.guiSlot, upg.getDisplayItem(player, false));
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backmeta = back.getItemMeta();
        backmeta.setDisplayName(ChatColor.GREEN + "Go Back");
        List<String> backlore = new ArrayList<>();
        backlore.add(ChatColor.GRAY + "To Prestige & Renown");
        backmeta.setLore(backlore);
        back.setItemMeta(backmeta);

        getInventory().setItem(49, back);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
