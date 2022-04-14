package dev.kyro.pitsim.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.EnchantingGUI;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.tutorial.TutorialManager;
import javafx.util.Pair;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TaintedWell implements Listener {

    public static Location wellLocation = new Location(Bukkit.getWorld("darkzone"), 10, 71, -94);
    public static ArmorStand wellStand;
    public static ArmorStand textLine1;
    public static ArmorStand textLine2;
    public static ArmorStand textLine3;
    public static ArmorStand textLine4;
    public static Map<Player, ArmorStand> removeStands = new HashMap<>();
    public static Map<Player, ArmorStand> enchantStands = new HashMap<>();
    public static List<Player> enchantingPlayers = new ArrayList<>();
    private static Map<Player, ItemStack> playerItems = new HashMap<>();
    public static int i = 0;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : wellStand.getNearbyEntities(25, 25, 25)) {
                    if(!(entity instanceof Player)) continue;
                    Player player = (Player) entity;
                    PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(wellStand),
                            (byte) 0, (byte) 0, (byte) 0, (byte) i, (byte) 0, false);
                    EntityPlayer nmsPlayer = ((CraftPlayer) entity).getHandle();
                    nmsPlayer.playerConnection.sendPacket(packet);


                    for (Map.Entry<Player, ArmorStand> entry : enchantStands.entrySet()) {
                        if(player == entry.getKey()) continue;

                        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(entry.getValue()));
                        nmsPlayer.playerConnection.sendPacket(destroyPacket);
                    }
                    for (Map.Entry<Player, ArmorStand> entry : removeStands .entrySet()) {
                        if(player == entry.getKey()) continue;

                        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(entry.getValue()));
                        nmsPlayer.playerConnection.sendPacket(destroyPacket);
                    }

                    if(!playerItems.containsKey(player)) {
//                        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(removeStand));
//                        nmsPlayer.playerConnection.sendPacket(destroyPacket);
//                        PacketPlayOutEntityDestroy enchantDestroyPacket = new PacketPlayOutEntityDestroy(getStandID(enchantStand));
//                        nmsPlayer.playerConnection.sendPacket(enchantDestroyPacket);
                    }

                    if(enchantingPlayers.contains(player)) {
                        i += 24;
                        player.playEffect(wellLocation.clone().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);
                    } else i += 8;
                    if(i >= 256) i = 0;
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 2, 2);
    }

    public static void onStart() {
        wellStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
        wellStand.setGravity(false);
        wellStand.setArms(true);
        wellStand.setVisible(false);

        textLine1 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 1, 0.5), ArmorStand.class);
        textLine1.setGravity(false);
        textLine1.setArms(true);
        textLine1.setVisible(false);
        textLine1.setCustomName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tained Well");
        textLine1.setCustomNameVisible(true);

        textLine2 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.7, 0.5), ArmorStand.class);
        textLine2.setGravity(false);
        textLine2.setArms(true);
        textLine2.setVisible(false);
        textLine2.setCustomName(ChatColor.GRAY + "Enchant Mystic Items found");
        textLine2.setCustomNameVisible(true);

        textLine3 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.4, 0.5), ArmorStand.class);
        textLine3.setGravity(false);
        textLine3.setArms(true);
        textLine3.setVisible(false);
        textLine3.setCustomName(ChatColor.GRAY + "in the Darkzone here");
        textLine3.setCustomNameVisible(true);

        textLine4 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.1, 0.5), ArmorStand.class);
        textLine4.setGravity(false);
        textLine4.setArms(true);
        textLine4.setVisible(false);
        textLine4.setCustomName(ChatColor.YELLOW + "Right-Click with an Item!");
        textLine4.setCustomNameVisible(true);


        wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
    }

    public static void onEnchant(Player player, ItemStack itemStack) {
        playerItems.put(player, itemStack);
        player.getInventory().remove(itemStack);
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(itemStack));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        showButtons(player);

    }

    public static void showButtons(Player player) {
        ArmorStand removeStand;
        removeStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
        removeStand.setGravity(false);
        removeStand.setArms(true);
        removeStand.setVisible(false);
        removeStand.setCustomName(ChatColor.RED + "Remove Item");
        removeStand.setCustomNameVisible(true);
        removeStands.put(player, removeStand);

        ArmorStand enchantStand;
        enchantStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
        enchantStand.setGravity(false);
        enchantStand.setArms(true);
        enchantStand.setVisible(false);
        enchantStand.setCustomName(ChatColor.GREEN + "Enchant Item");
        enchantStand.setCustomNameVisible(true);
        enchantStands.put(player, enchantStand);

        PacketPlayOutEntityEquipment removePacket = new PacketPlayOutEntityEquipment(getStandID(removeStand),  4, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
        PacketPlayOutEntityEquipment enchantPacket = new PacketPlayOutEntityEquipment(getStandID(enchantStand), 4, CraftItemStack.asNMSCopy(new ItemStack(new ItemStack(Material.EMERALD_BLOCK))));
//
//        for (Entity entity : wellStand.getNearbyEntities(7, 7, 7)) {
//            if(!(entity instanceof Player)) continue;
//            Player entityPlayer = (Player) entity;
//            EntityPlayer nmsPlayer = ((CraftPlayer) entityPlayer).getHandle();
//            nmsPlayer.playerConnection.sendPacket(packet);
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    nmsPlayer.playerConnection.sendPacket(removePacket);
//                    nmsPlayer.playerConnection.sendPacket(enchantPacket);
//                }
//            }.runTaskLater(PitSim.INSTANCE, 5);
//        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(removePacket);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(enchantPacket);
            }
        }.runTaskLater(PitSim.INSTANCE, 5);

        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) removeStand).getHandle()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

        PacketPlayOutSpawnEntityLiving enchantSpawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) enchantStand).getHandle()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(enchantSpawn);

        removeStand.teleport(removeStand.getLocation().clone().subtract(0, 0, 2));
        PacketPlayOutEntityTeleport tpPacket = new PacketPlayOutEntityTeleport(((CraftEntity) removeStand).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpPacket);

        enchantStand.teleport(enchantStand.getLocation().clone().add(0, 0, 2));
        PacketPlayOutEntityTeleport tpRemovePacket = new PacketPlayOutEntityTeleport(((CraftEntity) enchantStand).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpRemovePacket);

//        PacketPlayOutSpawnEntity entitySpawn = new PacketPlayOutSpawnEntity(((CraftEntity)removeStand).getHandle(), 30);
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entitySpawn);
//        PacketPlayOutEntityTeleport tpPacket = new PacketPlayOutEntityTeleport(getStandID(removeStand), wellLocation.getBlockX(), wellLocation.getBlockY(), wellLocation.getBlockZ(), (byte) 0, (byte) 0, false);
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpPacket);

        setText(player, "§c", "§c", "§c", "§c");
    }

    public static void onButtonPush(Player player, boolean enchant) {
        ArmorStand removeStand = removeStands.get(player);
        ArmorStand enchantStand = enchantStands.get(player);

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(removeStand),
                (byte) 0, (byte) 0, (byte) (2 * 32D), (byte) 0, (byte) 0, false);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpPacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                removeStands.remove(player);
                removeStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 2);


        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpRemovePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(enchantStand),
                (byte) 0, (byte) 0, (byte) (-2 * 32D), (byte) 0, (byte) 0, false);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpRemovePacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                enchantStands.remove(player);
                enchantStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 2);



        if(!enchant) {
            ItemStack item = playerItems.get(player);
            AUtil.giveItemSafely(player, item, true);
            playerItems.remove(player);
            enchantingPlayers.remove(player);

            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

            new BukkitRunnable() {
                @Override
                public void run() {
                    setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tained Well", ChatColor.GRAY + "Enchant Mystic Items found",
                            ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
                }
            }.runTaskLater(PitSim.INSTANCE, 3);

        } else {
            enchantingPlayers.add(player);
            setText(player, "§c", "§c", "§c", ChatColor.YELLOW + "Its rolling...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    enchantingPlayers.remove(player);
                    player.playEffect(wellLocation.clone().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);
                    showButtons(player);
                }
            }.runTaskLater(PitSim.INSTANCE, 80);
        }
    }

    @EventHandler
    public static void onEnchantingTableClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(block.getType() != Material.ENCHANTMENT_TABLE) return;
        if(player.getWorld() != Bukkit.getWorld("darkzone")) return;

        event.setCancelled(true);
        if(playerItems.containsKey(event.getPlayer())) return;
        if(TutorialManager.tutorials.containsKey(event.getPlayer())) return;

        if(Misc.isAirOrNull(player.getItemInHand())) return;
        NBTItem nbtItem = new NBTItem(player.getItemInHand());
        if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
            setText(player, "§c", ChatColor.RED + "Invalid Item!", ChatColor.RED + "Please try again!", "§c");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!playerItems.containsKey(player)) {
                        setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tained Well", ChatColor.GRAY + "Enchant Mystic Items found",
                                ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
                    }
                }
            }.runTaskLater(PitSim.INSTANCE, 40);

            return;
        }

        onEnchant(player, player.getItemInHand());

        Sounds.MYSTIC_WELL_OPEN_1.play(player);
        Sounds.MYSTIC_WELL_OPEN_2.play(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(playerItems.containsKey(event.getPlayer())) {
            onButtonPush(event.getPlayer(), false);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(!playerItems.containsKey(event.getPlayer())) return;
        if(event.getPlayer().getWorld() != Bukkit.getWorld("darkzone")) onButtonPush(event.getPlayer(), false);
        if(event.getPlayer().getLocation().distance(wellLocation) > 10) onButtonPush(event.getPlayer(), false);
    }

    @EventHandler
    public void onStandClick(PlayerInteractAtEntityEvent event) {
        if(!playerItems.containsKey(event.getPlayer())) return;
        for (ArmorStand value : removeStands.values()) {
            if(value.getUniqueId().equals(event.getRightClicked().getUniqueId())) onButtonPush(event.getPlayer(), false);
        }

        for (ArmorStand value : enchantStands.values()) {
            if(value.getUniqueId().equals(event.getRightClicked().getUniqueId())) onButtonPush(event.getPlayer(), true);
        }
    }

    public static int getStandID(ArmorStand stand) {
        for (Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(wellLocation, 5, 5, 5)) {
            if(!(entity instanceof ArmorStand)) continue;
            if(entity.getUniqueId().equals(stand.getUniqueId())) return entity.getEntityId();
        }
        return 0;
    }

    public static void setText(Player player, String line1, String line2, String line3, String line4) {

        if(line1 != null) {
            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) textLine1).getHandle()));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

            DataWatcher dw = ((CraftEntity) textLine1).getHandle().getDataWatcher();
            dw.watch(2, line1);
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine1), dw, false);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metaPacket);
        } else {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine1));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
        }

        if(line2 != null) {
            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) textLine2).getHandle()));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

            DataWatcher dw = ((CraftEntity) textLine2).getHandle().getDataWatcher();
            dw.watch(2, line2);
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine2), dw, false);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metaPacket);
        } else {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine2));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
        }

        if(line3 != null) {
            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) textLine3).getHandle()));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

            DataWatcher dw = ((CraftEntity) textLine3).getHandle().getDataWatcher();
            dw.watch(2, line3);
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine3), dw, false);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metaPacket);
        } else {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine3));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
        }

        if(line4 != null) {
            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(((EntityLiving)((CraftEntity) textLine4).getHandle()));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

            DataWatcher dw = ((CraftEntity) textLine4).getHandle().getDataWatcher();
            dw.watch(2, line4);
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine4), dw, false);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metaPacket);
        } else {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine4));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
        }





    }
}
