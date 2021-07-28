package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.Highlander;
import dev.kyro.pitsim.killstreaks.NoMegastreak;
import dev.kyro.pitsim.killstreaks.Overdrive;
import dev.kyro.pitsim.killstreaks.Uberstreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            if(slot == 22) {
                openPanel(perkGUI.getHomePanel());
            }

                for(Megastreak megastreak : PerkManager.megastreaks) {
                    if(megastreak.guiSlot() == slot) {
                        if(megastreak.getClass() == NoMegastreak.class) {
                            if(pitPlayer.playerLevel < 0) level = true;
                            if(pitPlayer.megastreak.getClass() == NoMegastreak.class) has = true;
                            if(!has && !level) {
                                pitPlayer.megastreak = new NoMegastreak(pitPlayer);
                                perkGUI.megaWrapUp();
                            }
                        } else if(megastreak.getClass() == Overdrive.class) {
                            if(pitPlayer.playerLevel < 0) level = true;
                            if(pitPlayer.megastreak.getClass() == Overdrive.class) has = true;
                            if(!has && !level) {
                                pitPlayer.megastreak = new Overdrive(pitPlayer);
                                perkGUI.megaWrapUp();
                            }
                        } else if(megastreak.getClass() == Highlander.class) {
                            if(pitPlayer.playerLevel < 10) level = true;
                            if(pitPlayer.megastreak.getClass() == Highlander.class) has = true;
                            if(!has && !level) {
                                pitPlayer.megastreak = new Highlander(pitPlayer);
                                perkGUI.megaWrapUp();
                            }
                        } else if(megastreak.getClass() == Uberstreak.class) {
                            if(pitPlayer.playerLevel < 20) level = true;
                            if(pitPlayer.megastreak.getClass() == Uberstreak.class) has = true;
                            if(!has && !level) {
                                pitPlayer.megastreak = new Uberstreak(pitPlayer);
                                perkGUI.megaWrapUp();
                            }
                        }
                        if(!level && !has) {
                            openPanel(perkGUI.getHomePanel());
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2F);
                        }
                        if(level) {
                            AOutput.error(player, "&cYou arent high enough level to use this");
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1F, 0.5F);
                        }
                        if(has) {
                            AOutput.error(player, "&cThat megastreak is already equipped");
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
            if(pitPlayer.megastreak.getClass() == megastreak.getClass() && megastreak.getClass() != NoMegastreak.class) {
                lore.add(ChatColor.GREEN + "Already selected!");
                meta.setDisplayName(ChatColor.GREEN + megastreak.getRawName());
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            } else if(pitPlayer.playerLevel < megastreak.levelReq() && megastreak.getClass() != NoMegastreak.class) {
                lore.add(ChatColor.RED + "Unlocked at level " + ChatColor.YELLOW + megastreak.levelReq() + ChatColor.RED + "!");
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
