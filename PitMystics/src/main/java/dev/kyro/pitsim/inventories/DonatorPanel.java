package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class DonatorPanel extends AGUIPanel {

    public DonatorGUI donatorGUI;
    public DonatorPanel(AGUI gui) {
        super(gui);
        donatorGUI = (DonatorGUI) gui;

        inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
    }

    @Override
    public String getName() {
        return "Donator GUI";
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 22) {

                openPanel(donatorGUI.killEffectPanel);
            } else if(slot == 23) {

                openPanel(donatorGUI.pantsColorPanel);
            }
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        getInventory().setItem(22, new ItemStack(Material.DIAMOND_BLOCK));
        getInventory().setItem(23, new ItemStack(Material.DIAMOND_BLOCK));
        updateInventory();


    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
