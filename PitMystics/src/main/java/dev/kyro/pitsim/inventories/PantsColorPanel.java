package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PantsColorPanel extends AGUIPanel {
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

    public DonatorGUI donatorGUI;
    public PantsColorPanel(AGUI gui) {
        super(gui);
        donatorGUI = (DonatorGUI) gui;
    }
    @Override
    public String getName() {
        return "Pants Colorizer";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 40) {
                openPanel(donatorGUI.getHomePanel());
            }

            if(Misc.isAirOrNull(getInventory().getItem(slot)) || !getInventory().getItem(slot).getType().equals(Material.LEATHER_LEGGINGS)) return;
            if(FreshCommand.isFresh(player.getInventory().getLeggings()) || Misc.isAirOrNull(player.getInventory().getLeggings())) return;
            if(!player.hasPermission("pitsim.pantscolor")) return;


            PantColor pantColor = PantColor.getPantColor(getInventory().getItem(slot));

            PantColor originalPantColor = PantColor.getPantColor(player.getInventory().getLeggings());
            assert originalPantColor != null;

            if(originalPantColor == PantColor.RED || originalPantColor == PantColor.BLUE ||
                    originalPantColor == PantColor.GREEN || originalPantColor == PantColor.YELLOW || originalPantColor == PantColor.ORANGE) {

                NBTItem nbtMystic = new NBTItem(player.getInventory().getLeggings());
                nbtMystic.setString(NBTTag.ORIGINAL_COLOR.getRef(), originalPantColor.refName);

                player.getInventory().setLeggings(nbtMystic.getItem());

            }

            if(slot == 41) {
                NBTItem nbtMystic = new NBTItem(player.getInventory().getLeggings());
                nbtMystic.removeKey(NBTTag.ORIGINAL_COLOR.getRef());

                player.getInventory().setLeggings(nbtMystic.getItem());
            }

            for (PantColor matchingPantColor : PantColor.values()) {
                if(matchingPantColor.equals(pantColor)) {
                    PantColor.setPantColor(player.getInventory().getLeggings(), pantColor);
                    Sounds.SUCCESS.play(player);
                    player.closeInventory();
                }
            }


            updateInventory();
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        int i = 9;


        for(PantColor pantColor : PantColor.values()) {
            if(pantColor.refName.equals("Blue") || pantColor.refName.equals("Red") || pantColor.refName.equals("Orange")
                    || pantColor.refName.equals("Yellow") || pantColor.refName.equals("Green") || pantColor.refName.equals("Jewel") || pantColor.refName.equals("Dark")) continue;

            ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemMeta meta = pants.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Premium Color");
            List<String> pantslore = new ArrayList<>();
            pantslore.add(ChatColor.translateAlternateColorCodes('&', "&7Color: " + pantColor.chatColor + pantColor.refName));
            pantslore.add(ChatColor.translateAlternateColorCodes('&', "&7Hex: " + pantColor.hexColor));
            pantslore.add("");
            if(player.getInventory().getLeggings() == null || player.getInventory().getLeggings().getType() != Material.LEATHER_LEGGINGS || !player.getInventory().getLeggings().hasItemMeta()) {
                pantslore.add(ChatColor.GRAY + "Wearing: " + ChatColor.RED + "None!");
            } else if(Objects.equals(PantColor.getPantColor(player.getInventory().getLeggings()), PantColor.JEWEL) || FreshCommand.isFresh(player.getInventory().getLeggings())) {
                pantslore.add(ChatColor.GRAY + "Wearing: " + ChatColor.RED + "Undyeable pants!");
            } else {
                pantslore.add(ChatColor.GRAY + "Wearing: " + player.getInventory().getLeggings().getItemMeta().getDisplayName());
            }
            pantslore.add("");
            pantslore.add(ChatColor.YELLOW + "Click to apply dye!");
            meta.setLore(pantslore);
            pants.setItemMeta(meta);
            PantColor.setPantColor(pants, pantColor);
            getInventory().setItem(i, pants);
            i++;
        }

        ItemStack playerPants = player.getInventory().getLeggings();
        PantColor originalColor = getOrginalColor(playerPants);

        if(originalColor != null) {




            ItemStack original = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemMeta originalMeta = original.getItemMeta();
            originalMeta.setDisplayName(ChatColor.GOLD + "Original Color");
            List<String> originalLore = new ArrayList<>();
            originalLore.add(ChatColor.translateAlternateColorCodes('&', "&7Color: "
                    + originalColor.chatColor + getOrginalColor(playerPants).refName));
            originalLore.add(ChatColor.GRAY + "Hex: " + getOrginalColor(playerPants).hexColor);
            originalLore.add("");
            originalLore.add(ChatColor.GRAY + "Wearing: " + playerPants.getItemMeta().getDisplayName());
            originalLore.add("");
            originalLore.add(ChatColor.YELLOW + "Click to add dye!");
            originalMeta.setLore(originalLore);
            original.setItemMeta(originalMeta);

            PantColor.setPantColor(original, originalColor);

            getInventory().setItem(41, original);
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backmeta = back.getItemMeta();
        backmeta.setDisplayName(ChatColor.GREEN + "Go Back");
        List<String> backlore = new ArrayList<>();
        backlore.add(ChatColor.GRAY + "To Donator Perks");
        backmeta.setLore(backlore);
        back.setItemMeta(backmeta);

        getInventory().setItem(40, back);

    }

    public PantColor getOrginalColor(ItemStack item) {
        if(Misc.isAirOrNull(item)) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null || !itemMeta.hasLore()) return null;


        NBTItem nbtItem = new NBTItem(item);

        if(!nbtItem.hasKey(NBTTag.ORIGINAL_COLOR.getRef())) return null;

        return PantColor.getPantColor(nbtItem.getString(NBTTag.ORIGINAL_COLOR.getRef()));
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
