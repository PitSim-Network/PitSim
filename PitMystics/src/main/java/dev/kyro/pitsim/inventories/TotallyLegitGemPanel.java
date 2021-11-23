package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotallyLegitGemPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    public Map<Integer, Integer> slots = new HashMap<>();
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

            int invSlot = slots.get(slot);

            for(int i = 0; i < player.getInventory().getSize(); i++) {
                if(i == invSlot) {
                    NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
                    nbtItem.setBoolean(NBTTag.IS_GEMMED.getRef(), true);

                    PitEnchant enchant = null;
                    Map<PitEnchant, Integer> enchants = EnchantManager.getEnchantsOnItem(nbtItem.getItem());
                    for(Map.Entry<PitEnchant, Integer> entry : enchants.entrySet()) {
                        if(entry.getValue() == 2) enchant = entry.getKey();
                    }

                    EnchantManager.setItemLore(nbtItem.getItem());
                    try {
                        player.getInventory().setItem(i, EnchantManager.addEnchant(nbtItem.getItem(), enchant, 3, false));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    player.closeInventory();
                    Sounds.GEM_USE.play(player);

                    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
                    if(pitPlayer.stats != null) pitPlayer.stats.itemsGemmed++;

                    int itemsToRemove = 1;
                    for(int j = 0; j < player.getInventory().getContents().length; j++) {
                        if(!Misc.isAirOrNull(player.getInventory().getItem(j))) {
                            NBTItem nbtItem2 = new NBTItem(player.getInventory().getItem(j));
                            if(nbtItem2.hasKey(NBTTag.IS_GEM.getRef())) {
                                int preAmount = player.getInventory().getItem(j).getAmount();
                                int newAmount = Math.max(0, preAmount - itemsToRemove);
                                itemsToRemove = Math.max(0, itemsToRemove - preAmount);
                                nbtItem2.getItem().setAmount(newAmount);
                                player.getInventory().setItem(j, nbtItem2.getItem());
                                if(itemsToRemove == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        int slot = 0;

        for(int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(Misc.isAirOrNull(item)) continue;

            NBTItem nbtItem = new NBTItem(item);
            if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef()) && !nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) {
                PitEnchant enchant = null;
                if(nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef()) < 3 || nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef()) < 8) continue;
                Map<PitEnchant, Integer> enchants = EnchantManager.getEnchantsOnItem(nbtItem.getItem());
                for(Map.Entry<PitEnchant, Integer> entry : enchants.entrySet()) {
                    if(entry.getValue() == 2) enchant = entry.getKey();
                }

                if(enchant == null || enchant.isRare) continue;
                ItemMeta meta = nbtItem.getItem().getItemMeta();
                List<String> lore = meta.getLore();
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to upgrade!");
                meta.setLore(lore);
                nbtItem.getItem().setItemMeta(meta);
                getInventory().setItem(slot, nbtItem.getItem());
                slots.put(slot, i);
                slot++;
            }

        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

}
