package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillEffect;
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

public class KillEffectPanel extends AGUIPanel {
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);


    public DonatorGUI donatorGUI;
    public KillEffectPanel(AGUI gui) {
        super(gui);
        donatorGUI = (DonatorGUI) gui;


        inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
    }

    @Override
    public String getName() {
        return "Kill Effects";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(NonManager.getNon(player) != null) return;
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

              if(slot == 22) {
                openPanel(donatorGUI.getHomePanel());
            }

              if(!player.hasPermission("pitsim.killeffect")) return;

            if(slot == 10) {
                if(pitPlayer.killEffect != null) {
                    pitPlayer.killEffect = null;
                    playerData.set("killeffect", null);
                    APlayerData.savePlayerData(player);
                    Sounds.SUCCESS.play(player);
                    openPanel(donatorGUI.deathCryPanel);
                } else {
                    Sounds.ERROR.play(player);
                    AOutput.error(player, "&cYou don't have a Kill effect equipped!");
                }
            } else if(slot == 11) {
                if(pitPlayer.killEffect != KillEffect.EXE_DEATH) {
                    pitPlayer.killEffect = KillEffect.EXE_DEATH;
                    playerData.set("killeffect", KillEffect.EXE_DEATH.toString());
                    APlayerData.savePlayerData(player);
                    Sounds.SUCCESS.play(player);
                    openPanel(donatorGUI.killEffectPanel);
                } else {
                    Sounds.ERROR.play(player);
                    AOutput.error(player, "&cThat kill effect is already equipped");
                }
                return;
            } else if(slot == 12) {
                if(pitPlayer.killEffect != KillEffect.FIRE) {
                    pitPlayer.killEffect = KillEffect.FIRE;
                    playerData.set("killeffect", KillEffect.FIRE.toString());
                    APlayerData.savePlayerData(player);
                    Sounds.SUCCESS.play(player);
                    openPanel(donatorGUI.killEffectPanel);
                } else {
                    Sounds.ERROR.play(player);
                    AOutput.error(player, "&cThat kill effect is already equipped");
                }
                return;
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
        if(pitPlayer.killEffect == null) {
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

        ItemStack exedeath = new ItemStack(Material.GOLD_SWORD);
        ItemMeta exedeathmeta = exedeath.getItemMeta();
        List<String> exedeathlore = new ArrayList<>();
        exedeathlore.add("");
        exedeathlore.add(ChatColor.translateAlternateColorCodes('&', "&7Watch the &cblood &7and hear the"));
        exedeathlore.add(ChatColor.translateAlternateColorCodes('&', "&cscreams &7of your enemies as they die"));
        exedeathlore.add(ChatColor.translateAlternateColorCodes('&', "&7Simulates &dRARE! &9Executioner"));
        exedeathlore.add("");
        if(pitPlayer.killEffect == KillEffect.EXE_DEATH) {
            exedeathmeta.setDisplayName(ChatColor.GREEN + "Executioner");
            exedeathlore.add(ChatColor.GREEN + "Already selected!");
            exedeathmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, false);
        } else {
            exedeathmeta.setDisplayName(ChatColor.YELLOW + "Executioner");
            exedeathlore.add(ChatColor.YELLOW + "Click to select!");
        }
        exedeathmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        exedeathmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        exedeathmeta.setLore(exedeathlore);
        exedeath.setItemMeta(exedeathmeta);


        ItemStack fire = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta firemeta = fire.getItemMeta();
        List<String> firelore = new ArrayList<>();
        firelore.add("");
        firelore.add(ChatColor.translateAlternateColorCodes('&', "&7Listen to the &6sizzle &7as you watch"));
        firelore.add(ChatColor.translateAlternateColorCodes('&', "&7your victims burst into &6flames"));
        firelore.add("");
        if(pitPlayer.killEffect == KillEffect.FIRE) {
            firemeta.setDisplayName(ChatColor.GREEN + "Fire");
            firelore.add(ChatColor.GREEN + "Already selected!");
            firemeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, false);
        } else {
            firemeta.setDisplayName(ChatColor.YELLOW + "Fire");
            firelore.add(ChatColor.YELLOW + "Click to select!");
        }
        firemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        firemeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        firemeta.setLore(firelore);
        fire.setItemMeta(firemeta);

        getInventory().setItem(10, none);
        getInventory().setItem(11, exedeath);
        getInventory().setItem(12, fire);
        getInventory().setItem(22, back);
        updateInventory();

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
