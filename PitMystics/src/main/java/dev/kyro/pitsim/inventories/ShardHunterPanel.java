package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.ShardHunter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
                    if(upgrade.prestigeReq > pitPlayer.prestige) {
                        AOutput.error(player, "&cYou are too low level to acquire this!");
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
                if(getShards() < 64) {
                    AOutput.error(player, "&cYou do not have enough shards to craft this!");
                    Sounds.NO.play(player);
                    return;
                }

                int itemsToRemove = 64;
                for(int i = 0; i < player.getInventory().getContents().length; i++) {
                    if(!Misc.isAirOrNull(player.getInventory().getItem(i))) {
                        NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
                        if(nbtItem.hasKey(NBTTag.IS_SHARD.getRef())) {
                            int preAmount = player.getInventory().getItem(i).getAmount();
                            int newAmount = Math.max(0, preAmount - itemsToRemove);
                            itemsToRemove = Math.max(0, itemsToRemove - preAmount);
                            nbtItem.getItem().setAmount(newAmount);
                            player.getInventory().setItem(i, nbtItem.getItem());
                            if(itemsToRemove == 0) {
                                break;
                            }
                        }
                    }
                }

                AUtil.giveItemSafely(player, ShardHunter.getGemItem(), true);
                player.closeInventory();
                Sounds.GEM_CRAFT.play(player);
                AOutput.send(player, "&d&lITEM CRAFTED! &7Received &aTotally Legit Gem&7!");

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
            if(renownUpgrade.refName.equals("SHARDHUNTER")) upgrade = renownUpgrade;
        }

        StringBuilder output = new StringBuilder();
        int shards = getShards();
        double calc = 0.3906; //Alternately 0.3906 25/64
        int filled = (int) (calc * (shards + 1));
        if(filled > 25) filled = 25;
        int remaining = 25 - filled;

        for(int i = 0; i < filled; i++) {
            output.append(ChatColor.GREEN + "|");
        }
        for(int i = 0; i < remaining; i++) {
            output.append(ChatColor.GRAY + "|");
        }

        ItemStack gem = new ItemStack(Material.WORKBENCH);
        ItemMeta meta = gem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Craft Totally Legit Gem");
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Use &f64 &aAncient Gem Shards &7to craft"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7a&a Totally Legit Gem&7. (Gain a 9th token"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7on &dHidden Jewel &7items)"));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Your progress:"));
        float percent = 100 * (((float) shards) / 64);
        if(percent > 100) percent = 100;
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a" + shards + "&7/64 ") + output.toString() + ChatColor.DARK_GRAY + " (" + (int) percent + "%)");
        lore.add("");
        if(shards >= 64)lore.add(ChatColor.YELLOW + "Click to craft!");
        else lore.add(ChatColor.RED + "Not enough shards!");
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

    public int getShards() {
        int shards = 0;
        for(ItemStack itemStack : player.getInventory()) {
            if(Misc.isAirOrNull(itemStack)) continue;
            NBTItem nbtItem = new NBTItem(itemStack);
            if(nbtItem.hasKey(NBTTag.IS_SHARD.getRef())) {
                shards += itemStack.getAmount();
            }
        }
        return shards;
    }


    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
