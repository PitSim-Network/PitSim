package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
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
import java.util.List;

public class WithercraftPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    RenownUpgrade upgrade = null;
    public RenownShopGUI renownShopGUI;
    public WithercraftPanel(AGUI gui) {
        super(gui);
        renownShopGUI = (RenownShopGUI) gui;

    }

    @Override
    public String getName() {
        return "Withercraft";
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
                    if(upgrade.prestigeReq > pitPlayer.prestige) {
                        AOutput.error(player, "&cYou are too low prestige to acquire this!");
                        Sounds.NO.play(player);
                        return;
                    }
                    if(upgrade.isTiered) {
                        if(upgrade.maxTiers != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            Sounds.NO.play(player);
                            return;
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
                            return;
                        }
                        RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                        openPanel(renownShopGUI.renownShopConfirmPanel);
                    } else {
                        AOutput.error(player, "&aYou already unlocked this upgrade!");
                        Sounds.NO.play(player);
                    }

            }
            if(slot == 11) {
                if(pitPlayer.renown < 5) {
                    AOutput.error(player, "&cYou do not have enough renown to do this!");
                    Sounds.NO.play(player);
                    return;
                }

                openPanel(RenownShopGUI.itemClearPanel);
//                player.closeInventory();
//                ASound.play(player, Sound.ORB_PICKUP, 2, 1.5F);
//                AOutput.send(player, "&d&lITEM CRAFTED! &7Received &aTotally Legit Gem&7!");

            }
            if(slot == 22) {
                openPanel(renownShopGUI.getHomePanel());
            }
            APlayerData.savePlayerData(player);
            updateInventory();
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        for(RenownUpgrade renownUpgrade : UpgradeManager.upgrades) {
            if(renownUpgrade.refName.equals("WITHERCRAFT")) upgrade = renownUpgrade;
        }


        ItemStack gem = new ItemStack(Material.EMPTY_MAP);
        ItemMeta meta = gem.getItemMeta();
        if(pitPlayer.renown >= 5)meta.setDisplayName(ChatColor.YELLOW + "Clear Hidden Jewel Item");
        else meta.setDisplayName(ChatColor.RED + "Clear Hidden Jewel Item");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Clear all non-jewel enchants"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7off of a Hidden Jewel item."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7If a &aTotally Legit Gem &7has been"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7used on the item, receive &f32-64"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&aAncient Gem Shards&7."));
        lore.add("");
        if(pitPlayer.renown >= 5)lore.add(ChatColor.YELLOW + "Clear item for 5 renown!");
        else lore.add(ChatColor.RED + "Not enough renown!");
        meta.setLore(lore);
        gem.setItemMeta(meta);

        getInventory().setItem(11, gem);
        getInventory().setItem(15, upgrade.getDisplayItem(player, true));

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.GREEN + "Go Back");
        List<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "To Renown Shop");
        backMeta.setLore(backLore);
        back.setItemMeta(backMeta);

        getInventory().setItem(22, back);

    }


    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
