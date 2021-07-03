package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.misc.Misc;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DonatorPanel extends AGUIPanel {
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

    public DonatorGUI donatorGUI;
    public DonatorPanel(AGUI gui) {
        super(gui);
        donatorGUI = (DonatorGUI) gui;

        inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
    }

    @Override
    public String getName() {
        return "Donator Perks";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 19) {
                if(!player.hasPermission("pitsim.pantscolor")) return;
                openPanel(donatorGUI.pantsColorPanel);
            } else if(slot == 20) {
                if(!player.hasPermission("pitsim.deathcry")) return;
                openPanel(donatorGUI.deathCryPanel);
            } else if(slot == 21) {
                if(!player.hasPermission("pitsim.killeffect")) return;
                openPanel(donatorGUI.killEffectPanel);
            }  else if(slot == 22) {
                if(!player.hasPermission("pitsim.itemrename")) return;
                ItemStack heldItem = player.getItemInHand();
                if(Misc.isAirOrNull(heldItem)) return;
                NBTItem nbtItem = new NBTItem(heldItem);
                if(nbtItem.hasKey(NBTTag.PIT_ENCHANT_ORDER.getRef()) && !nbtItem.hasKey(NBTTag.JEWEL_KILLS.getRef())) {
                    player.closeInventory();
                    ItemRename.renameItem(player, player.getItemInHand());
                }
            } else if(slot == 23) {
                if(!player.hasPermission("pitsim.chatcolor")) return;
                openPanel(donatorGUI.chatColorPanel);
            }
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta pantsmeta = pants.getItemMeta();
        List<String> pantslore = new ArrayList<>();
        pantslore.add(ChatColor.GRAY + "Choose from a selection of premium");
        pantslore.add(ChatColor.GRAY + "colors to dye your pants with");
        pantslore.add("");
        if(player.hasPermission("pitsim.pantscolor")) {
            pantslore.add(ChatColor.YELLOW + "Click to select!");
            pantsmeta.setDisplayName(ChatColor.YELLOW + "Pants Colorizer");
        } else {
            pantslore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &5Overpowered &crank!"));
            pantsmeta.setDisplayName(ChatColor.RED + "Pants Colorizer");
        }
        pantsmeta.setLore(pantslore);
        pants.setItemMeta(pantsmeta);
        PantColor.setPantColor(pants, PantColor.HARVEST_RED);



        ItemStack death = new ItemStack(Material.GHAST_TEAR);
        ItemMeta deathmeta = death.getItemMeta();
        List<String> deathlore = new ArrayList<>();
        deathlore.add(ChatColor.GRAY + "Choose from a variety of sound effects");
        deathlore.add(ChatColor.GRAY + "that play to others when you die");
        deathlore.add("");
        if(pitPlayer.deathCry != null && player.hasPermission("pitsim.deathcry")) {
            deathlore.add(ChatColor.GRAY + "Selected: " + ChatColor.GREEN + pitPlayer.deathCry.refName);
            deathlore.add("");
        }
        if(player.hasPermission("pitsim.deathcry")) {
            deathlore.add(ChatColor.YELLOW + "Click to select!");
            deathmeta.setDisplayName(ChatColor.YELLOW + "Death Cries");
        } else {
            deathlore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &5Overpowered &crank!"));
            deathmeta.setDisplayName(ChatColor.RED + "Death Cries");
        }
        deathmeta.setLore(deathlore);
        death.setItemMeta(deathmeta);


        ItemStack kill = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta killmeta = kill.getItemMeta();
        List<String> killlore = new ArrayList<>();
        killlore.add(ChatColor.GRAY + "Choose from a variety of sound and");
        killlore.add(ChatColor.GRAY + "particle effects that play when you");
        killlore.add(ChatColor.GRAY + "kill an enemy");
        killlore.add("");
        if(pitPlayer.killEffect != null && player.hasPermission("pitsim.killeffect")) {
            killlore.add(ChatColor.GRAY + "Selected: " + ChatColor.GREEN + pitPlayer.killEffect.refName);
            killlore.add("");
        }
        if(player.hasPermission("pitsim.killeffect")) {
            killlore.add(ChatColor.YELLOW + "Click to select!");
            killmeta.setDisplayName(ChatColor.YELLOW + "Kill Effects");
        } else {
            killlore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &3Extraordinary &crank!"));
            killmeta.setDisplayName(ChatColor.RED + "Kill Effects");
        }
        killmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        killmeta.setLore(killlore);
        kill.setItemMeta(killmeta);


        ItemStack rename = new ItemStack(Material.NAME_TAG);
        ItemMeta renamemeta = rename.getItemMeta();
        List<String> renamelore = new ArrayList<>();
        renamelore.add(ChatColor.GRAY + "Rename any mystic item that you're");
        renamelore.add(ChatColor.GRAY + "holding to anything you want");
        renamelore.add("");
        ItemStack heldItem = player.getItemInHand();
        if(player.hasPermission("pitsim.itemrename")) {
            if(!Misc.isAirOrNull(heldItem)) {
                NBTItem nbtItem = new NBTItem(heldItem);
                if(nbtItem.hasKey(NBTTag.PIT_ENCHANT_ORDER.getRef()) && !nbtItem.hasKey(NBTTag.JEWEL_KILLS.getRef())) {
                    renamelore.add(ChatColor.GRAY + "Holding: " + heldItem.getItemMeta().getDisplayName());
                    renamelore.add("");
                    renamelore.add(ChatColor.YELLOW + "Click to select!");
                    renamemeta.setDisplayName(ChatColor.YELLOW + "Rename Item");
                } else {
                    renamelore.add(ChatColor.GRAY + "Holding: " + ChatColor.RED + "Invalid Item!");
                    renamelore.add("");
                    renamelore.add(ChatColor.RED + "Must be holding a mystic item!");
                    renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
                }
            } else {
                renamelore.add(ChatColor.GRAY + "Holding: " + ChatColor.RED + "Invalid Item!");
                renamelore.add("");
                renamelore.add(ChatColor.RED + "Must be holding a mystic item!");
                renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
            }
        } else {
            renamelore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &bMiraculous &crank!"));
            renamemeta.setDisplayName(ChatColor.RED + "Rename Item");
        }
        renamemeta.setLore(renamelore);
        rename.setItemMeta(renamemeta);


        ItemStack chat = new ItemStack(Material.SIGN);
        ItemMeta chatmeta = chat.getItemMeta();
        List<String>  chatlore = new ArrayList<>();
        chatlore.add(ChatColor.GRAY + "Choose any color to send your chat");
        chatlore.add(ChatColor.GRAY + "messages in");
        chatlore.add("");
        if(player.hasPermission("pitsim.chatcolor")) {
            if(ChatColorPanel.playerChatColors.containsKey(player)) {
                chatlore.add(ChatColor.GRAY + "Selected: " + ChatColorPanel.playerChatColors.get(player).chatColor + ChatColorPanel.playerChatColors.get(player).refName);
                chatlore.add("");
            }
            chatlore.add(ChatColor.YELLOW + "Click to select!");
            chatmeta.setDisplayName(ChatColor.YELLOW + "Chat Color");
        } else {
            chatlore.add(ChatColor.translateAlternateColorCodes('&', "&cRequires &6Unthinkable &crank!"));
            chatmeta.setDisplayName(ChatColor.RED + "Chat Color");
        }
        chatmeta.setLore(chatlore);
        chat.setItemMeta(chatmeta);

        getInventory().setItem(19, pants);
        getInventory().setItem(20, death);
        getInventory().setItem(21, kill);
        getInventory().setItem(22, rename);
        getInventory().setItem(23, chat);

        updateInventory();


    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
