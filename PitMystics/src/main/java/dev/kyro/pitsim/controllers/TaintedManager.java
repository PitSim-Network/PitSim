package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TaintedManager implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
                    ItemStack item = event.getPlayer().getInventory().getItem(i);
                    if(Misc.isAirOrNull(item)) continue;

                    NBTItem nbtItem = new NBTItem(item);
                    if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) continue;

                    ItemMeta meta = nbtItem.getItem().getItemMeta();
                    if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {
                        if(item.getType() == Material.GOLD_SWORD || item.getType() == Material.STONE_SWORD ||
                                item.getType() == Material.LEATHER_LEGGINGS ||
                                item.getType() == Material.CHAINMAIL_LEGGINGS)meta.setLore(scramble(meta.getLore()));
                        else meta.setLore(descramble(meta.getLore()));
                        if(item.getType() == Material.GOLD_SWORD) nbtItem.getItem().setType(Material.STONE_SWORD);
                        else if(item.getType() == Material.LEATHER_LEGGINGS) nbtItem.getItem().setType(Material.CHAINMAIL_LEGGINGS);
                        else if(item.getType() == Material.STONE_HOE) nbtItem.getItem().setType(Material.GOLD_HOE);
                        else if(item.getType() == Material.CHAINMAIL_CHESTPLATE) nbtItem.getItem().setType(Material.LEATHER_CHESTPLATE);
                    } else {
                        if(item.getType() == Material.GOLD_SWORD || item.getType() == Material.STONE_SWORD ||
                                item.getType() == Material.LEATHER_LEGGINGS ||
                                item.getType() == Material.CHAINMAIL_LEGGINGS)meta.setLore(descramble(meta.getLore()));
                        else meta.setLore(scramble(meta.getLore()));
                        if(item.getType() == Material.GOLD_HOE) nbtItem.getItem().setType(Material.STONE_HOE);
                        else if(item.getType() == Material.LEATHER_CHESTPLATE) nbtItem.getItem().setType(Material.CHAINMAIL_CHESTPLATE);
                        else if(item.getType() == Material.STONE_SWORD) nbtItem.getItem().setType(Material.GOLD_SWORD);
                        else if(item.getType() == Material.CHAINMAIL_LEGGINGS) nbtItem.getItem().setType(Material.LEATHER_LEGGINGS);
                    }
                    nbtItem.getItem().setItemMeta(meta);

                    event.getPlayer().getInventory().setItem(i, nbtItem.getItem());
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 2);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
                    ItemStack item = event.getPlayer().getInventory().getItem(i);
                    if(Misc.isAirOrNull(item)) continue;

                    NBTItem nbtItem = new NBTItem(item);
                    if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) continue;

                    ItemMeta meta = nbtItem.getItem().getItemMeta();
                    if(event.getPlayer().getWorld() == MapManager.getDarkzone()) meta.setLore(scramble(meta.getLore()));
                    else meta.setLore(descramble(meta.getLore()));
                    nbtItem.getItem().setItemMeta(meta);

                    event.getPlayer().getInventory().setItem(i, nbtItem.getItem());
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 1);
    }

    public static String scramble(String msg) {
        StringBuilder builder = new StringBuilder();

        char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '§') {
                builder.append(chars[i]);
                builder.append(chars[i + 1]);
                builder.append("§k");
                i++;
            } else builder.append(chars[i]);
        }

        return builder.toString();
    }

    public static List<String> scramble(List<String> messages) {
        List<String> finishedStrings = new ArrayList<>();

        for (String msg : messages) {
            StringBuilder builder = new StringBuilder();

            char[] chars = msg.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if(chars[i] == '§') {
                    builder.append(chars[i]);
                    builder.append(chars[i + 1]);
                    builder.append("§k");
                    i++;
                } else builder.append(chars[i]);
            }
            finishedStrings.add(builder.toString());
        }

        return finishedStrings;
    }


    public static String descramble(String msg) {
        StringBuilder builder = new StringBuilder();

        char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i + 1 > chars.length) break;
            if(chars[i] == '§' && chars[i + 1] == 'k') {
                i += 2;
            } else builder.append(chars[i]);
        }

        return builder.toString();
    }

    public static List<String> descramble(List<String> messages) {
        List<String> finishedStrings = new ArrayList<>();

        for (String msg : messages) {
            StringBuilder builder = new StringBuilder();

            char[] chars = msg.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if(i + 1 > chars.length) break;
                if(chars[i] == '§' && chars[i + 1] == 'k') {
                    i++;
                } else builder.append(chars[i]);
            }
            finishedStrings.add(builder.toString());
        }

        return finishedStrings;
    }
}
