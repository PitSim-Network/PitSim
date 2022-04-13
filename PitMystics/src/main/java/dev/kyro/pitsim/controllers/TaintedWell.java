package dev.kyro.pitsim.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.inventories.EnchantingGUI;
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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaintedWell implements Listener {

    public static Location wellLocation = new Location(Bukkit.getWorld("darkzone"), 10, 71, -94);
    public static ArmorStand wellStand;
    public static Map<Player, ArmorStand> removeStands = new HashMap<>();
    public static Map<Player, ArmorStand> enchantStands = new HashMap<>();
    public static int standID;
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
                    } else {
                        ArmorStand removeStand = removeStands.get(player);
                        player.playEffect(wellLocation.clone().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);

//                        PacketPlayOutEntityEquipment removePacket = new PacketPlayOutEntityEquipment(getStandID(removeStand) + 1, 1, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
//                        PacketPlayOutEntityEquipment enchantPacket = new PacketPlayOutEntityEquipment(getStandID(enchantStand) + 1, 1, CraftItemStack.asNMSCopy(new ItemStack(Material.EMERALD_BLOCK)));
//                        nmsPlayer.playerConnection.sendPacket(removePacket);
//                        nmsPlayer.playerConnection.sendPacket(enchantPacket);

                    }

                    i += 8;
                    if(i == 256) i = 0;
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 2, 2);
    }

    public static void onStart() {
        wellStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
        wellStand.setGravity(false);
        wellStand.setArms(true);
        wellStand.setVisible(false);


        wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
    }

    public static void onEnchant(Player player, ItemStack itemStack) {
        playerItems.put(player, itemStack);
        player.getInventory().remove(itemStack);

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


        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(itemStack));
        PacketPlayOutEntityEquipment removePacket = new PacketPlayOutEntityEquipment(getStandID(removeStand),  4, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
        PacketPlayOutEntityEquipment enchantPacket = new PacketPlayOutEntityEquipment(getStandID(enchantStand), 4, CraftItemStack.asNMSCopy(new ItemStack(new ItemStack(Material.EMERALD_BLOCK))));

        for (Entity entity : wellStand.getNearbyEntities(7, 7, 7)) {
            if(!(entity instanceof Player)) continue;
            Player entityPlayer = (Player) entity;
            EntityPlayer nmsPlayer = ((CraftPlayer) entityPlayer).getHandle();
            nmsPlayer.playerConnection.sendPacket(packet);
            new BukkitRunnable() {
                @Override
                public void run() {
                    nmsPlayer.playerConnection.sendPacket(removePacket);
                    nmsPlayer.playerConnection.sendPacket(enchantPacket);
                }
            }.runTaskLater(PitSim.INSTANCE, 5);
        }

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

    }

    public static void onButtonPush(Player player, boolean enchant) {
        ArmorStand removeStand = removeStands.get(player);
        ArmorStand enchantStand = enchantStands.get(player);

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(removeStand),
                (byte) 0, (byte) 0, (byte) (2 * 8D), (byte) 0, (byte) 0, false);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpPacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                removeStands.remove(player);
                removeStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 1);


        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpRemovePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(enchantStand),
                (byte) 0, (byte) 0, (byte) (-2 * 8D), (byte) 0, (byte) 0, false);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tpRemovePacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                enchantStands.remove(player);
                enchantStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 1);

        player.playEffect(wellLocation.clone().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);

        if(!enchant) {
            ItemStack item = playerItems.get(player);
            AUtil.giveItemSafely(player, item);
            playerItems.remove(player);

            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        }


    }

    @EventHandler
    public static void onEnchantingTableClick(PlayerInteractEvent event) {
        if(TutorialManager.tutorials.containsKey(event.getPlayer())) return;
        if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(block.getType() != Material.ENCHANTMENT_TABLE) return;
        if(player.getWorld() != Bukkit.getWorld("darkzone")) return;

        event.setCancelled(true);
        onEnchant(player, player.getItemInHand());

        Sounds.MYSTIC_WELL_OPEN_1.play(player);
        Sounds.MYSTIC_WELL_OPEN_2.play(player);
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
}
