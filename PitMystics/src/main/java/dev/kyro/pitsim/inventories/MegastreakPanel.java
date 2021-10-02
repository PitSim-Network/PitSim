package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.*;
import dev.kyro.pitsim.upgrades.UberIncrease;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class MegastreakPanel extends AGUIPanel {
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    public PerkGUI perkGUI;
    public MegastreakPanel(AGUI gui) {
        super(gui);
        perkGUI = (PerkGUI) gui;

        inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
    }

    @Override
    public String getName() {
        return "Choose a Megastreak";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        boolean level = false;
        boolean has = false;
        boolean uberCd = false;

        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 22) {
                openPanel(perkGUI.getHomePanel());
            }

            for(Megastreak megastreak : PerkManager.megastreaks) {
                if(megastreak.guiSlot() == slot) {
                    PitPlayer pitPlayer = PitPlayer.getPitPlayer(perkGUI.player);
                    pitPlayer.setKills(0);
                    if(megastreak.getClass() == NoMegastreak.class) {
                        if(pitPlayer.prestige < 0) level = true;
                        if(pitPlayer.megastreak.getClass() == NoMegastreak.class) has = true;
                        if(!has && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new NoMegastreak(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Overdrive.class) {
                        if(pitPlayer.prestige < 0) level = true;
                        if(pitPlayer.megastreak.getClass() == Overdrive.class) has = true;
                        if(!has && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Overdrive(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Highlander.class) {
                        if(pitPlayer.prestige < 25) level = true;
                        if(pitPlayer.megastreak.getClass() == Highlander.class) has = true;
                        if(!has && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Highlander(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Beastmode.class) {
                        if(pitPlayer.prestige < 16) level = true;
                        if(pitPlayer.megastreak.getClass() == Beastmode.class) has = true;
                        if(!has && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Beastmode(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Uberstreak.class) {
                        if(pitPlayer.prestige < 20) level = true;
                        if(pitPlayer.megastreak.getClass() == Uberstreak.class) has = true;
                        if(pitPlayer.dailyUbersLeft <= 0) uberCd = true;
                        if(!has && !level && !uberCd) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Uberstreak(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    }
                    if(!level && !has && !uberCd) {
                        openPanel(perkGUI.getHomePanel());
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2F);
                        FileConfiguration playerData = APlayerData.getPlayerData(player);
                        playerData.set("megastreak", megastreak.getRawName());
                        APlayerData.savePlayerData(player);
                    }
                    if(level) {
                        AOutput.error(player, "&cYou arent high enough level to use this");
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
                    }
                    if(has) {
                        AOutput.error(player, "&cThat megastreak is already equipped");
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
                    }
                    if(uberCd) {
                        AOutput.error(player, "&cYou have reached the daily limit for this killstreak");
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
                    }
                }
            }
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        for(Megastreak megastreak : PerkManager.megastreaks) {
            ItemStack item = new ItemStack(megastreak.guiItem().getType());
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>(megastreak.guiItem().getItemMeta().getLore());
            lore.add("");
            if(megastreak.getClass() == Uberstreak.class && pitPlayer.prestige >= megastreak.prestigeReq()) {
                if((System.currentTimeMillis() / 1000L) - 86400 > pitPlayer.uberReset) {
                    pitPlayer.uberReset = 0;
                    pitPlayer.dailyUbersLeft = 5 + UberIncrease.getUberIncrease(player);
                }
                int ubersLeft = pitPlayer.dailyUbersLeft;
                if(ubersLeft == 0) lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &c0&7/" + (5 + UberIncrease.getUberIncrease(player))));
                else lore.add(ChatColor.translateAlternateColorCodes('&', "&dDaily Uberstreaks remaining: &a" + ubersLeft + "&7/" + (5 + UberIncrease.getUberIncrease(player))));

                FileConfiguration playerData = APlayerData.getPlayerData(pitPlayer.player);
                playerData.set("ubercooldown", pitPlayer.uberReset);
                playerData.set("ubersleft", pitPlayer.dailyUbersLeft);
                APlayerData.savePlayerData(pitPlayer.player);
            }
            if(pitPlayer.megastreak.getClass() == megastreak.getClass() && megastreak.getClass() != NoMegastreak.class) {
                lore.add(ChatColor.GREEN + "Already selected!");
                meta.setDisplayName(ChatColor.GREEN + megastreak.getRawName());
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            } else if(pitPlayer.prestige < megastreak.prestigeReq() && megastreak.getClass() != NoMegastreak.class) {
                lore.add(ChatColor.RED + "Unlocked at prestige " + ChatColor.YELLOW + AUtil.toRoman(megastreak.prestigeReq()));
                meta.setDisplayName(ChatColor.RED + megastreak.getRawName());
            } else if(megastreak.getClass() == Uberstreak.class && pitPlayer.dailyUbersLeft == 0){
                lore.add(ChatColor.RED + "Daily limit reached!");
                meta.setDisplayName(ChatColor.RED + megastreak.getRawName());
            } else if(megastreak.getClass() != NoMegastreak.class){
                lore.add(ChatColor.YELLOW + "Click to select!");
                meta.setDisplayName(ChatColor.YELLOW + megastreak.getRawName());
            }
            if(megastreak.getRawName().equalsIgnoreCase("Uberstreak")) {
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            }
            if(megastreak.getClass() == NoMegastreak.class) {
                meta.setDisplayName(ChatColor.RED +  megastreak.getRawName());
                lore.add(ChatColor.YELLOW + "Click to remove megastreak!");
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLore(lore);
            item.setItemMeta(meta);

            getInventory().setItem(megastreak.guiSlot(), item);

        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Go Back");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "To Perks");
        meta.setLore(lore);
        back.setItemMeta(meta);

        getInventory().setItem(22, back);


    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
