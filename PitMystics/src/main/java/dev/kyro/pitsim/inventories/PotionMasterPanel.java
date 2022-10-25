package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionMasterPanel extends AGUIPanel {
    public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    public PotionMasterGUI potionMasterGUI;

    public Map<Integer, Integer> potionMap = new HashMap<>();

    public PotionMasterPanel(AGUI gui) {
        super(gui);
        potionMasterGUI = (PotionMasterGUI) gui;
    }

    @Override
    public String getName() {
        return "Create Splash Potions";
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory().getHolder() == this) {
            int slot = event.getSlot();

            for (Map.Entry<Integer, Integer> entry : potionMap.entrySet()) {
                if(slot != entry.getKey()) continue;
                ItemStack item = player.getInventory().getItem(entry.getValue()).clone();

                if(Misc.isAirOrNull(item)) {
                    player.closeInventory();
                    return;
                }

                NBTItem nbtItem = new NBTItem(item);
                int cost = nbtItem.getInteger(NBTTag.POTION_POTENCY.getRef());

                if(pitPlayer.taintedSouls >= cost) {
                    ItemStack splashPotion = PotionManager.createSplashPotion(item);
                    player.getInventory().setItem(entry.getValue(), splashPotion);

                    pitPlayer.taintedSouls -= cost;
                    player.closeInventory();

                    Sounds.SPLASH_CRAFT1.play(player);
                    Sounds.SPLASH_CRAFT2.play(player);

                    return;
                } else {
                    AOutput.error(player, "&c&lNOPE! &7Not enough souls!");
                    Sounds.NO.play(player);
                }
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        int j = 0;

        for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(Misc.isAirOrNull(item)) continue;

            item = item.clone();

            NBTItem nbtItem = new NBTItem(item);
            if(!nbtItem.hasKey(NBTTag.POTION_IDENTIFIER.getRef())) continue;
            if(nbtItem.hasKey(NBTTag.IS_SPLASH_POTION.getRef())) continue;

            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            int cost  = nbtItem.getInteger(NBTTag.POTION_POTENCY.getRef());
            lore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + cost + " Tainted Souls");
            lore.add(ChatColor.GRAY + "You have: " + ChatColor.WHITE + pitPlayer.taintedSouls + " Tainted Souls");
            lore.add("");
            lore.add(pitPlayer.taintedSouls >= cost ? ChatColor.YELLOW + "Click to make Splash Potion!" : ChatColor.RED + "Not enough Tainted Souls!");
            meta.setLore(lore);
            item.setItemMeta(meta);

            potionMap.put(j, i);
            getInventory().setItem(j, item);
            j++;

        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
