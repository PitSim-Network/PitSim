package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.UberIncrease;
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
        boolean prestige = false;
        boolean has = false;
        boolean level = false;
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
                        if(pitPlayer.prestige < 0) prestige = true;
                        if(pitPlayer.level < 0) level = true;
                        if(pitPlayer.megastreak.getClass() == NoMegastreak.class) has = true;
                        if(!has && !prestige && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new NoMegastreak(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Overdrive.class) {
                        if(pitPlayer.prestige < 0) prestige = true;
                        if(pitPlayer.level < 0) level = true;
                        if(pitPlayer.megastreak.getClass() == Overdrive.class) has = true;
                        if(!has && !prestige && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Overdrive(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Highlander.class) {
                        if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
                        if(pitPlayer.megastreak.getClass() == Highlander.class) has = true;
                        if(pitPlayer.level < 0) level = true;
                        if(pitPlayer.level < 80) level = true;
                        if(!has && !prestige && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Highlander(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Beastmode.class) {
                        if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
                        if(pitPlayer.megastreak.getClass() == Beastmode.class) has = true;
                        if(pitPlayer.level < 50) level = true;
                        if(!has && !prestige && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Beastmode(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == Uberstreak.class) {
                        if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
                        if(pitPlayer.megastreak.getClass() == Uberstreak.class) has = true;
                        if(pitPlayer.level < 100) level = true;
                        if(pitPlayer.dailyUbersLeft <= 0) uberCd = true;
                        if(!has && !prestige && !uberCd && !level) {
                            pitPlayer.megastreak.stop();
                            pitPlayer.megastreak = new Uberstreak(pitPlayer);
                            perkGUI.megaWrapUp();
                        }
                    } else if(megastreak.getClass() == ToTheMoon.class) {
                    if(pitPlayer.prestige < megastreak.prestigeReq()) prestige = true;
                    if(pitPlayer.megastreak.getClass() == ToTheMoon.class) has = true;
                        if(pitPlayer.level < 50) level = true;
                    if(!has && !prestige && !uberCd && !level) {
                        pitPlayer.megastreak.stop();
                        pitPlayer.megastreak = new ToTheMoon(pitPlayer);
                        perkGUI.megaWrapUp();
                    }
                }
                    if(!prestige && !has && !uberCd && !level) {
                        openPanel(perkGUI.getHomePanel());
                        Sounds.SUCCESS.play(player);
                        FileConfiguration playerData = APlayerData.getPlayerData(player);
                        playerData.set("megastreak", megastreak.getRawName());
                        APlayerData.savePlayerData(player);
                    }
                    if(prestige) {
                        AOutput.error(player, "&cYou aren't high enough prestige to use this!");
                        Sounds.ERROR.play(player);
                    }
                    if(has) {
                        AOutput.error(player, "&cThat megastreak is already equipped");
                        Sounds.ERROR.play(player);
                    }
                    if(level) {
                        AOutput.error(player, "&cYou are not high enough level to use this megastreak!");
                        Sounds.ERROR.play(player);
                    }
                    if(uberCd) {
                        AOutput.error(player, "&cYou have reached the daily limit for this killstreak");
                        Sounds.ERROR.play(player);
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
            } else if(pitPlayer.level < megastreak.levelReq()){
                PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
                lore.add(ChatColor.translateAlternateColorCodes('&', "&cUnlocked at level " + info.getOpenBracket() + PrestigeValues.getLevelColor(megastreak.levelReq()) + megastreak.levelReq() + info.getCloseBracket()));
                meta.setDisplayName(ChatColor.RED + megastreak.getRawName());
            }else if(megastreak.getClass() != NoMegastreak.class){
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
