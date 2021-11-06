package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.misc.Sounds;
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

public class DeathCryPanel extends AGUIPanel {
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    FileConfiguration playerData = APlayerData.getPlayerData(player);

    public DonatorGUI donatorGUI;
    public DeathCryPanel(AGUI gui) {
        super(gui);
        donatorGUI = (DonatorGUI) gui;

        inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
    }

    @Override
    public String getName() {
        return "Death Cries";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {

        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {


            if(slot == 22) {
                openPanel(donatorGUI.getHomePanel());
            }

            if(!player.hasPermission("pitsim.deathcry")) return;
                if(slot == 10) {
                    if(pitPlayer.deathCry != null) {
                        pitPlayer.deathCry = null;
                        playerData.set("deathcry", null);
                        APlayerData.savePlayerData(player);
                        Sounds.SUCCESS.play(player);
                        openPanel(donatorGUI.deathCryPanel);
                    } else {
                        Sounds.ERROR.play(player);
                        AOutput.error(player, "&cYou don't have a Death cry equipped!");
                    }
              } else if(slot == 11) {
                if(pitPlayer.deathCry != DeathCry.MARIO_DEATH) {
                    pitPlayer.deathCry = DeathCry.MARIO_DEATH;
                    playerData.set("deathcry", DeathCry.MARIO_DEATH.toString());
                    APlayerData.savePlayerData(player);
                    Sounds.SUCCESS.play(player);
                    openPanel(donatorGUI.deathCryPanel);
                } else {
                    Sounds.ERROR.play(player);
                    AOutput.error(player, "&cThat death cry is already equipped");
                }
                return;
            } else if(slot == 12) {
                if(pitPlayer.deathCry != DeathCry.GHAST_SCREAM) {
                    pitPlayer.deathCry = DeathCry.GHAST_SCREAM;
                    playerData.set("deathcry", DeathCry.GHAST_SCREAM.toString());
                    APlayerData.savePlayerData(player);
                    Sounds.SUCCESS.play(player);
                    openPanel(donatorGUI.deathCryPanel);
                } else {
                    Sounds.ERROR.play(player);
                    AOutput.error(player, "&cThat death cry is already equipped");
                }
            }

            updateInventory();


        }

        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backmeta = back.getItemMeta();
        backmeta.setDisplayName(ChatColor.GREEN + "Go Back");
        List<String> backlore = new ArrayList<>();
        backlore.add(ChatColor.GRAY + "To Donator Perks");
        backmeta.setLore(backlore);
        back.setItemMeta(backmeta);

        ItemStack none = new ItemStack(Material.BARRIER);
        ItemMeta nonemeta = none.getItemMeta();
        List<String> nonelore = new ArrayList<>();
        nonelore.add("");
        if(pitPlayer.deathCry == null) {
            nonelore.add(ChatColor.GREEN + "Already Selected!");
            nonemeta.setDisplayName(ChatColor.GREEN + "None");
            nonemeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,0 , false);
            nonemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            nonelore.add(ChatColor.YELLOW + "Click to select!");
            nonemeta.setDisplayName(ChatColor.YELLOW + "None");
        }
        nonemeta.setLore(nonelore);
        none.setItemMeta(nonemeta);

        ItemStack mario = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta mariometa = mario.getItemMeta();
        List<String> mariolore = new ArrayList<>();
        mariolore.add("");
        mariolore.add(ChatColor.translateAlternateColorCodes('&', "&7Let out the death tune from &cSuper"));
        mariolore.add(ChatColor.translateAlternateColorCodes('&', "&cMario &7as you die"));
        mariolore.add("");
        if(pitPlayer.deathCry == DeathCry.MARIO_DEATH) {
            mariometa.setDisplayName(ChatColor.GREEN + "Super Mario");
            mariolore.add(ChatColor.GREEN + "Already selected!");
            mariometa.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, false);
        } else {
            mariometa.setDisplayName(ChatColor.YELLOW + "Super Mario");
            mariolore.add(ChatColor.YELLOW + "Click to select!");
        }
        mariometa.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        mariometa.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mariometa.setLore(mariolore);
        mario.setItemMeta(mariometa);


        ItemStack ghast = new ItemStack(Material.FIREBALL);
        ItemMeta ghastmeta = ghast.getItemMeta();
        List<String> ghastlore = new ArrayList<>();
        ghastlore.add("");
        ghastlore.add(ChatColor.translateAlternateColorCodes('&', "&7Your last words are a &cbloody scream"));
        ghastlore.add(ChatColor.translateAlternateColorCodes('&', "&7similar to those of a ghast"));
        ghastlore.add("");
        if(pitPlayer.deathCry == DeathCry.GHAST_SCREAM) {
            ghastmeta.setDisplayName(ChatColor.GREEN + "Ghast Scream");
            ghastlore.add(ChatColor.GREEN + "Already selected!");
            ghastmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, false);
        } else {
            ghastmeta.setDisplayName(ChatColor.YELLOW + "Ghast Scream");
            ghastlore.add(ChatColor.YELLOW + "Click to select!");
        }
        ghastmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ghastmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ghastmeta.setLore(ghastlore);
        ghast.setItemMeta(ghastmeta);

        getInventory().setItem(10, none);
        getInventory().setItem(11, mario);
        getInventory().setItem(12, ghast);
        getInventory().setItem(22, back);
        updateInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
