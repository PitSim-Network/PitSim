package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TotallyLegitGemPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    public GemGUI gemGUI;
    public TotallyLegitGemPanel(AGUI gui) {
        super(gui);
        gemGUI = (GemGUI) gui;

    }

    @Override
    public String getName() {
        return "Totally Legit Selector";
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {


        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        int slot = 0;

        for(ItemStack item : player.getInventory()) {
            if(Misc.isAirOrNull(item)) continue;

            NBTItem nbtItem = new NBTItem(item);
            if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef()) && !nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) {
                if(nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef()) < 3 || nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef()) < 8) continue;
                ItemMeta meta = nbtItem.getItem().getItemMeta();
                List<String> lore = meta.getLore();
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to upgrade!");
                meta.setLore(lore);
                nbtItem.getItem().setItemMeta(meta);
                getInventory().setItem(slot, nbtItem.getItem());
                slot++;
            }

        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

}
