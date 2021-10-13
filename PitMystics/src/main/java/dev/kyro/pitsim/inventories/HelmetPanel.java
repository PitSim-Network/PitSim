package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HelmetPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(player.getItemInHand(), player);
    public HelmetGUI helmetGUI;
    public HelmetPanel(AGUI gui) {
        super(gui);
        helmetGUI = (HelmetGUI) gui;

    }

    @Override
    public String getName() {
        return "Modify Helmet";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {
            if(slot == 7) {
                HelmetGUI.deposit(player, goldenHelmet.item);
                player.closeInventory();
            }

        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        List<String> helmetLore = goldenHelmet.item.getItemMeta().getLore();
        helmetLore.remove(helmetLore.size() - 1);
        helmetLore.remove(helmetLore.size() - 1);
        ItemStack helmetDisplay = goldenHelmet.item.clone();
        ItemMeta helmetMeta = helmetDisplay.getItemMeta();
        helmetMeta.setLore(helmetLore);
        helmetDisplay.setItemMeta(helmetMeta);
        getInventory().setItem(1, helmetDisplay);

        getInventory().setItem(4, new ItemStack(Material.BEACON));

        ItemStack deposit = new ItemStack(Material.PAPER);
        ItemMeta depositMeta = deposit.getItemMeta();
        depositMeta.setDisplayName(ChatColor.YELLOW + "Deposit Gold");
        List<String> depositLore = new ArrayList<>();
        depositLore.add(ChatColor.translateAlternateColorCodes('&', "&7Deposit &6gold &7into the helmet that"));
        depositLore.add(ChatColor.translateAlternateColorCodes('&', "&7can be spent to use abilities and"));
        depositLore.add(ChatColor.translateAlternateColorCodes('&', "&7builds up passives the more that's"));
        depositLore.add(ChatColor.translateAlternateColorCodes('&', "&7in it."));
        depositLore.add("");
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        depositLore.add(ChatColor.GRAY + "Current gold: " + ChatColor.GOLD + formatter.format(goldenHelmet.gold) + "g");
        depositLore.add("");
        depositLore.add(ChatColor.YELLOW + "Click to deposit gold!");

        depositMeta.setLore(depositLore);
        deposit.setItemMeta(depositMeta);
        getInventory().setItem(7, deposit);



    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

}
