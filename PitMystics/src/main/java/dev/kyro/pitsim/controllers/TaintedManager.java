package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaintedManager implements Listener {

    public static List<Player> players = new ArrayList<>();

    @EventHandler
    public void onHeal(HealEvent event) {
        if(!event.isPlayer) return;
        if(!MapManager.inDarkzone(event.player)) return;
        event.multipliers.add(0.5);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Arrow) return;
        if(event.getEntity() instanceof CraftArrow) return;
        if(event.getEntity() instanceof Fireball) return;
        if(!MapManager.inDarkzone((LivingEntity) event.getDamager()) || !MapManager.inDarkzone((LivingEntity) event.getEntity())) return;

        if(event.getDamager() instanceof Player) {
            ItemStack held = ((Player) event.getDamager()).getItemInHand();
            if(!Misc.isAirOrNull(held) && held.getType() == Material.GOLD_HOE) {
                double multiplier = Misc.isCritical((Player) event.getDamager()) ? 1.5 : 1;
                event.setDamage(8 * multiplier);
            } else if(!Misc.isAirOrNull(held) && held.getType() == Material.STONE_SWORD) {
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(players.contains(event.getPlayer())) return;
        if(!Bukkit.getOnlinePlayers().contains(event.getPlayer())) return;
        players.add(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {

                if(!Misc.isAirOrNull(event.getPlayer().getInventory().getChestplate())) {
                    NBTItem chest = new NBTItem(event.getPlayer().getInventory().getChestplate());
                    if(chest.hasKey(NBTTag.ITEM_UUID.getRef())) {
                        LeatherArmorMeta meta;
                        if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {
                            chest.getItem().setType(Material.LEATHER_CHESTPLATE);
                            meta = (LeatherArmorMeta) chest.getItem().getItemMeta();
                            meta.setColor(Color.fromRGB(PantColor.TAINTED.hexColor));
                            chest.getItem().setItemMeta(meta);
                        } else {
                            chest.getItem().setType(Material.CHAINMAIL_CHESTPLATE);
                        }
                        event.getPlayer().getInventory().setChestplate(chest.getItem());
                    }
                }

                if(!Misc.isAirOrNull(event.getPlayer().getInventory().getLeggings())) {
                    NBTItem pants = new NBTItem(event.getPlayer().getInventory().getLeggings());
                    if(pants.hasKey(NBTTag.ITEM_UUID.getRef())) {
                        LeatherArmorMeta meta;
                        if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {
                            pants.getItem().setType(Material.CHAINMAIL_LEGGINGS);
                        } else {
                            pants.getItem().setType(Material.LEATHER_LEGGINGS);
                            meta = (LeatherArmorMeta) pants.getItem().getItemMeta();
                            if(pants.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
                                meta.setColor(Color.fromRGB(Objects.requireNonNull(PantColor.getPantColor(pants.getString(NBTTag.SAVED_PANTS_COLOR.getRef()))).hexColor));
                            pants.getItem().setItemMeta(meta);
                            }
                        }
                        event.getPlayer().getInventory().setLeggings(pants.getItem());
                    }
                }



                for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
                    ItemStack item = event.getPlayer().getInventory().getItem(i);
                    if(Misc.isAirOrNull(item)) continue;

                    NBTItem nbtItem = new NBTItem(item);
                    if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) continue;

                    LeatherArmorMeta leatherMeta = null;
                    ItemMeta meta = nbtItem.getItem().getItemMeta();

                    if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {

                        switch (MysticType.getMysticType(item)) {
                            case SWORD:
                                meta.setLore(scramble(meta.getLore()));
                                nbtItem.getItem().setType(Material.STONE_SWORD);
                                break;
                            case PANTS:
//                                nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), Objects.requireNonNull(PantColor.getPantColor(nbtItem.getItem())).refName);
                                nbtItem.getItem().setType(Material.CHAINMAIL_LEGGINGS);
                                break;
                            case TAINTED_SCYTHE:
                                meta.setLore(descramble(meta.getLore()));
                                nbtItem.getItem().setType(Material.GOLD_HOE);
                                break;
                            case TAINTED_CHESTPLATE:
                                nbtItem.getItem().setType(Material.LEATHER_CHESTPLATE);
                                leatherMeta = (LeatherArmorMeta) nbtItem.getItem().getItemMeta();
                                leatherMeta.setColor(Color.fromRGB(PantColor.TAINTED.hexColor));
                                break;
                        }

                    } else {

                        switch (MysticType.getMysticType(item)) {
                            case SWORD:
                                meta.setLore(descramble(meta.getLore()));
                                nbtItem.getItem().setType(Material.GOLD_SWORD);
                                break;
                            case PANTS:
                                nbtItem.getItem().setType(Material.LEATHER_LEGGINGS);
                                leatherMeta = (LeatherArmorMeta) nbtItem.getItem().getItemMeta();
                                if(nbtItem.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
                                    leatherMeta.setColor(Color.fromRGB(Objects.requireNonNull(PantColor.getPantColor(nbtItem.getString(NBTTag.SAVED_PANTS_COLOR.getRef()))).hexColor));
                                }
                                break;
                            case TAINTED_SCYTHE:
                                meta.setLore(scramble(meta.getLore()));
                                nbtItem.getItem().setType(Material.STONE_HOE);
                                break;
                            case TAINTED_CHESTPLATE:
                                nbtItem.getItem().setType(Material.CHAINMAIL_CHESTPLATE);
                                 break;
                        }

                    }

                    if(leatherMeta != null) nbtItem.getItem().setItemMeta(leatherMeta);
                    else {
                        nbtItem.getItem().setItemMeta(meta);
                    }

                    event.getPlayer().getInventory().setItem(i, nbtItem.getItem());
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 5);






        new BukkitRunnable() {
            @Override
            public void run() {

                if(!Misc.isAirOrNull(event.getPlayer().getInventory().getChestplate())) {
                    NBTItem chest = new NBTItem(event.getPlayer().getInventory().getChestplate());
                    if(chest.hasKey(NBTTag.ITEM_UUID.getRef())) {
                        ItemMeta meta = chest.getItem().getItemMeta();
                        if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {
                            meta.setLore(descramble(meta.getLore()));
                        } else {
                            meta.setLore(scramble(meta.getLore()));
                        }
                        chest.getItem().setItemMeta(meta);
                        event.getPlayer().getInventory().setChestplate(chest.getItem());
                    }
                }

                if(!Misc.isAirOrNull(event.getPlayer().getInventory().getLeggings())) {
                    NBTItem pants = new NBTItem(event.getPlayer().getInventory().getLeggings());
                    if(pants.hasKey(NBTTag.ITEM_UUID.getRef())) {
                        ItemMeta meta = pants.getItem().getItemMeta();
                        if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {
                            meta.setLore(scramble(meta.getLore()));
                        } else {
                            meta.setLore(descramble(meta.getLore()));
                        }
                        pants.getItem().setItemMeta(meta);
                        event.getPlayer().getInventory().setLeggings(pants.getItem());
                    }
                }

                for (int i = 0; i < event.getPlayer().getInventory().getSize(); i++) {
                    ItemStack item = event.getPlayer().getInventory().getItem(i);
                    if(Misc.isAirOrNull(item)) continue;

                    NBTItem nbtItem = new NBTItem(item);
                    if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) continue;

                    ItemMeta meta = nbtItem.getItem().getItemMeta();

                    if(event.getPlayer().getWorld() == MapManager.getDarkzone()) {

                        if(MysticType.getMysticType(item) == MysticType.PANTS) {
                            meta.setLore(scramble(meta.getLore()));
                        } else if(MysticType.getMysticType(item) == MysticType.TAINTED_CHESTPLATE) {
                            meta.setLore(descramble(meta.getLore()));
                        }

                    } else {

                        if(MysticType.getMysticType(item) == MysticType.PANTS) {
                            meta.setLore(descramble(item.getItemMeta().getLore()));
                        } else if(MysticType.getMysticType(item) == MysticType.TAINTED_CHESTPLATE) {
                            meta.setLore(scramble(meta.getLore()));
                        }

                    }

                    nbtItem.getItem().setItemMeta(meta);


                    event.getPlayer().getInventory().setItem(i, nbtItem.getItem());
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                players.remove(event.getPlayer());
            }
        }.runTaskLater(PitSim.INSTANCE, 40);
    }

    @EventHandler
    public void onAttack(AttackEvent.Pre event) {
        Player player = event.attackerPlayer;
        if(player == null) return;

        if(player.getWorld() == MapManager.getDarkzone()) {
            if(!Misc.isAirOrNull(player.getItemInHand())) {
                NBTItem nbtItem = new NBTItem(player.getItemInHand());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.SWORD) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
                        event.getAttackerEnchantMap().remove(pitEnchant);
                    }
                }
            }

            if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
                NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getLeggings()) == MysticType.PANTS) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getLeggings()).keySet()) {
                        event.getAttackerEnchantMap().remove(pitEnchant);
                    }
                }
            }

        } else {

            if(!Misc.isAirOrNull(player.getItemInHand())) {
                NBTItem nbtItem = new NBTItem(player.getItemInHand());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
                        event.getAttackerEnchantMap().remove(pitEnchant);
                    }
                }
            }

            if(!Misc.isAirOrNull(player.getInventory().getChestplate())) {
                NBTItem nbtItem = new NBTItem(player.getInventory().getChestplate());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getChestplate()) == MysticType.TAINTED_CHESTPLATE) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getChestplate()).keySet()) {
                        event.getAttackerEnchantMap().remove(pitEnchant);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onDefend(AttackEvent.Pre event) {
        Player player = event.defenderPlayer;
        if(player == null) return;

        if(player.getWorld() == MapManager.getDarkzone()) {
            if(!Misc.isAirOrNull(player.getItemInHand())) {
                NBTItem nbtItem = new NBTItem(player.getItemInHand());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.SWORD) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
                        event.getDefenderEnchantMap().remove(pitEnchant);
                    }
                }
            }

            if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
                NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
                if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getLeggings()) == MysticType.PANTS) {
                    for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getLeggings()).keySet()) {
                        event.getDefenderEnchantMap().remove(pitEnchant);
                    }
                }
            }

        } else {

            if(player.getWorld() == MapManager.getDarkzone()) {
                if(!Misc.isAirOrNull(player.getItemInHand())) {
                    NBTItem nbtItem = new NBTItem(player.getItemInHand());
                    if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
                        for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
                            event.getDefenderEnchantMap().remove(pitEnchant);
                        }
                    }
                }

                if(!Misc.isAirOrNull(player.getInventory().getChestplate())) {
                    NBTItem nbtItem = new NBTItem(player.getInventory().getChestplate());
                    if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getChestplate()) == MysticType.TAINTED_CHESTPLATE) {
                        for (PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getChestplate()).keySet()) {
                            event.getDefenderEnchantMap().remove(pitEnchant);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if(players.contains((Player) event.getPlayer())) event.setCancelled(true);
    }


    public static String scramble(String msg) {
        StringBuilder builder = new StringBuilder();

        char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '\u00A7') {
                builder.append(chars[i]);
                builder.append(chars[i + 1]);
                builder.append("\u00A7k");
                i++;
            } else builder.append(chars[i]);
        }

        return builder.toString();
    }

    public static List<String> scramble(List<String> messages) {
        if(messages == null) return null;
        List<String> finishedStrings = new ArrayList<>();

        for (String msg : messages) {
            StringBuilder builder = new StringBuilder();

            char[] chars = msg.toCharArray();
            for (int i = 0; i < chars.length; i++) {

                if(i + 1 <= chars.length && chars[i] == '\u00A7' && chars[i + 1] == 'k'){
                    return messages;
                }
                if(chars[i] == '\u00A7') {
                    builder.append(chars[i]);
                    builder.append(chars[i + 1]);
                    builder.append("\u00A7k");
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
            if(chars[i] == '\u00A7' && chars[i + 1] == 'k') {
                i += 2;
            } else builder.append(chars[i]);
        }

        return builder.toString();
    }

    public static List<String> descramble(List<String> messages) {
        if(messages == null) return null;
        List<String> finishedStrings = new ArrayList<>();

        for (String msg : messages) {
            StringBuilder builder = new StringBuilder();

            char[] chars = msg.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if(i + 1 > chars.length) break;
                if(chars[i] == '\u00A7' && chars[i + 1] == 'k') {
                    i++;
                } else builder.append(chars[i]);
            }
            finishedStrings.add(builder.toString());
        }

        return finishedStrings;
    }
}
