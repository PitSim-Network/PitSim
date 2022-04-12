package dev.kyro.pitsim.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import javafx.util.Pair;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaintedWell {

    public static Location wellLocation = new Location(Bukkit.getWorld("darkzone"), 50, 79, -90);
    public static ArmorStand wellStand;
    private static Map<Player, ItemStack> playerItems = new HashMap<>();

    public static void onStart() {
        wellStand = wellLocation.getWorld().spawn(wellLocation, ArmorStand.class);
        wellStand.setGravity(false);
        wellStand.setArms(true);

        wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
    }

    public static void onEnchant(Player player, ItemStack itemStack) {
        playerItems.put(player, itemStack);
        player.getInventory().remove(itemStack);

        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(wellStand.getEntityId(), 0, CraftItemStack.asNMSCopy(new ItemStack(itemStack.getType())));
        for (Entity entity : wellStand.getNearbyEntities(7, 7, 7)) {
            if(!(entity instanceof Player)) continue;
            Player entityPlayer = (Player) entity;
            EntityPlayer nmsPlayer = ((CraftPlayer) entityPlayer).getHandle();
            Bukkit.broadcastMessage(itemStack + "");
            nmsPlayer.playerConnection.sendPacket(packet);
            Bukkit.broadcastMessage(entity + "");
        }
    }
}
