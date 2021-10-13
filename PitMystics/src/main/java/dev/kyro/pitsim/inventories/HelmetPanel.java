package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.HelmetSystem;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
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
    List<List<ItemStack>> columns = new ArrayList<>();
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


        List<ItemStack> column1 = new ArrayList<>();
        column1.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column1.add(new ItemStack(Material.GOLD_INGOT));
        column1.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column1);

        List<ItemStack> column2 = new ArrayList<>();
        column2.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column2.add(new ItemStack(Material.GOLD_INGOT));
        column2.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column2);

        List<ItemStack> column3 = new ArrayList<>();
        column3.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column3.add(new ItemStack(Material.GOLD_INGOT));
        column3.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column3);

        List<ItemStack> column4 = new ArrayList<>();
        column4.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column4.add(new ItemStack(Material.GOLD_INGOT));
        column4.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column4);

        List<ItemStack> column5 = new ArrayList<>();
        column5.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column5.add(new ItemStack(Material.GOLD_INGOT));
        column5.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column5);

        List<ItemStack> column6 = new ArrayList<>();
        column6.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column6.add(new ItemStack(Material.GOLD_INGOT));
        column6.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column6);

        List<ItemStack> column7 = new ArrayList<>();
        column7.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column7.add(new ItemStack(Material.GOLD_INGOT));
        column7.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column7);

        List<ItemStack> column8 = new ArrayList<>();
        column8.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column8.add(new ItemStack(Material.GOLD_INGOT));
        column8.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column8);

        List<ItemStack> column9 = new ArrayList<>();
        column9.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        column9.add(new ItemStack(Material.GOLD_INGOT));
        column9.add(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        columns.add(column9);

        int level = HelmetSystem.getLevel(goldenHelmet.gold);

        int i = 1;
        if(level < 3) i = 1;
        else if(level > 94) i = 92;
        else i = level - 4;

        for(int j = 0; j < 9; j++) {
            setColumn(j, i);
            i++;
        }





    }

    public void setColumn(int column, int level) {
        List<ItemStack> columnList = columns.get(column);
            if(HelmetSystem.getTotalGoldAtLevel(level) < goldenHelmet.gold) {
                columnList.get(0).setDurability((short) 5);
                getInventory().setItem(column + 9, columnList.get(0));
                columnList.get(2).setDurability((short) 5);
                getInventory().setItem(column + 27, columnList.get(2));

            } else if(HelmetSystem.getLevel(goldenHelmet.gold) > 1 && HelmetSystem.getTotalGoldAtLevel(level - 1) <= goldenHelmet.gold) {
                columnList.get(0).setDurability((short) 1);
                getInventory().setItem(column + 9, columnList.get(0));
                columnList.get(2).setDurability((short) 1);
                getInventory().setItem(column + 27, columnList.get(2));
            } else {
                Bukkit.broadcastMessage(column + " " + HelmetSystem.getLevel(goldenHelmet.gold));
                if(HelmetSystem.getLevel(goldenHelmet.gold) == 1 && column == 0) {
                    Bukkit.broadcastMessage("test");
                    columnList.get(0).setDurability((short) 1);
                    columnList.get(2).setDurability((short) 1);
                }
                getInventory().setItem(column + 9, columnList.get(0));
                getInventory().setItem(column + 27, columnList.get(2));
            }


        List<HelmetSystem.Passive> passives;
        if(HelmetSystem.getLevel(goldenHelmet.gold) == 1) passives = HelmetSystem.getLevelData(level + 1);
        else passives = HelmetSystem.getLevelData(level);
        if(passives.size() > 1) getInventory().setItem(column + 18, new ItemStack(Material.BEACON));
        else if(passives.size() == 1){
            columnList.get(1).setType(Material.INK_SACK);
            columnList.get(1).setDurability(passives.get(0).data);
            getInventory().setItem(column + 18, columnList.get(1));
        } else getInventory().setItem(column + 18, columnList.get(1));

    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

}


