package dev.kyro.pitsim.brewing.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.BrewingManager;
import dev.kyro.pitsim.controllers.TaintedWell;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.*;

public class BrewingAnimation {

    public Location location;
    public List<ArmorStand> stands = new ArrayList<>();
    public List<Player> players = new ArrayList<>();
    
    public Map<Player, ArmorStand> identityStands = new HashMap<>();
    public Map<Player, ArmorStand> potencyStands = new HashMap<>();
    public Map<Player, ArmorStand> durationStands = new HashMap<>();
    public Map<Player, ArmorStand> brewingTimeStands = new HashMap<>();
    public Map<Player, ArmorStand> confirmStands = new HashMap<>();
    public Map<Player, ArmorStand> cancelStands = new HashMap<>();

    public BrewingAnimation(Location location) {
        this.location = location;
        location.getBlock().setType(Material.CAULDRON);

        List<String> originalMessages = Arrays.asList("&e&lBrewing Stand", "&7Use &5Tainted &7mob drops", "&7to change different", "&7aspects of the potion", "&eRight-Click the Barriers!");

        for (int i = 0; i < 5; i++) {
            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, (0.3 * (i + 1)), 0.5), EntityType.ARMOR_STAND);
            stand.setCustomNameVisible(true);
            stand.setCustomName(ChatColor.translateAlternateColorCodes('&', originalMessages.get(originalMessages.size() - (i + 1))));
            stand.setGravity(false);
            stand.setVisible(false);
            stands.add(stand);
            BrewingManager.brewingStands.add(stand);

        }
    }

    public void setText(Player player, String[] text) {
        for (int i = 0; i < stands.size(); i++) {
            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity) stands.get(i)).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
            DataWatcher dw = ((CraftEntity)stands.get(i)).getHandle().getDataWatcher();
            if(text[text.length - (i + 1)] == null) dw.watch(2, "§c");
            else dw.watch(2, ChatColor.translateAlternateColorCodes('&', text[text.length - (i + 1)]));
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(stands.get(i)), dw, false);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
        }
    }

    public int getStandID(final ArmorStand stand) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, 5.0, 5.0, 5.0)) {
            if (!(entity instanceof ArmorStand)) continue;
            if (entity.getUniqueId().equals(stand.getUniqueId())) return entity.getEntityId();
        }
        return 0;
    }

    public void addPlayer(Player player) {
        players.add(player);
        showButtons(player);
    }
    
    public void showButtons(Player player) {
        ArmorStand identityStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        identityStand.setCustomNameVisible(true);
        identityStand.setCustomName(ChatColor.LIGHT_PURPLE + "Potion Type");
        identityStand.setGravity(false);
        identityStand.setVisible(false);
        identityStand.setArms(true);
        identityStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
        identityStands.put(player, identityStand);
        BrewingManager.brewingStands.add(identityStand);

        ArmorStand potencyStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        potencyStand.setCustomNameVisible(true);
        potencyStand.setCustomName(ChatColor.LIGHT_PURPLE + "Potion Potency");
        potencyStand.setGravity(false);
        potencyStand.setVisible(false);
        potencyStand.setArms(true);
        potencyStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
        potencyStands.put(player, potencyStand);
        BrewingManager.brewingStands.add(potencyStand);

        ArmorStand durationStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        durationStand.setCustomNameVisible(true);
        durationStand.setCustomName(ChatColor.LIGHT_PURPLE + "Potion Duration");
        durationStand.setGravity(false);
        durationStand.setVisible(false);
        durationStand.setArms(true);
        durationStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
        durationStands.put(player, durationStand);
        BrewingManager.brewingStands.add(durationStand);

        ArmorStand brewingTimeStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        brewingTimeStand.setCustomNameVisible(true);
        brewingTimeStand.setCustomName(ChatColor.LIGHT_PURPLE + "Brewing Time");
        brewingTimeStand.setGravity(false);
        brewingTimeStand.setVisible(false);
        brewingTimeStand.setArms(true);
        brewingTimeStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
        brewingTimeStands.put(player, brewingTimeStand);
        BrewingManager.brewingStands.add(brewingTimeStand);

        ArmorStand confirmStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, -0.8, 1.5), EntityType.ARMOR_STAND);
        confirmStand.setCustomNameVisible(true);
        confirmStand.setCustomName(ChatColor.GREEN + "Confirm");
        confirmStand.setGravity(false);
        confirmStand.setVisible(false);
        confirmStand.setArms(true);
        confirmStand.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
        confirmStands.put(player, confirmStand);
        BrewingManager.brewingStands.add(confirmStand);

        ArmorStand cancelStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, -0.8, -0.5), EntityType.ARMOR_STAND);
        cancelStand.setCustomNameVisible(true);
        cancelStand.setCustomName(ChatColor.RED + "Cancel");
        cancelStand.setGravity(false);
        cancelStand.setVisible(false);
        cancelStand.setArms(true);
        cancelStand.setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
        cancelStands.put(player, cancelStand);
        BrewingManager.brewingStands.add(cancelStand);

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(identityStand), (byte) 0, (byte) 0, (byte) -127, (byte) 0, (byte) 0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(identityTpPacket);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook potencyTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(potencyStand), (byte) 0, (byte) 0, (byte) -64, (byte) 0, (byte) 0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(potencyTpPacket);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook durationTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(durationStand), (byte) 0, (byte) 0, (byte) 64, (byte) 0, (byte) 0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(durationTpPacket);
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook brewingTimeTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(brewingTimeStand), (byte) 0, (byte) 0, (byte) 127, (byte) 0, (byte) 0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(brewingTimeTpPacket);


        new BukkitRunnable() {
            @Override
            public void run() {
                identityStand.teleport(identityStand.getLocation().clone().subtract(0, 0, 3.97));
                identityStand.getLocation().setPitch(64);
                potencyStand.teleport(potencyStand.getLocation().clone().subtract(0, 0, 2));
                durationStand.teleport(durationStand.getLocation().clone().add(0, 0, 2));
                brewingTimeStand.teleport(brewingTimeStand.getLocation().clone().add(0, 0, 3.97));
            }
        }.runTaskLater(PitSim.INSTANCE, 10);

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(identityStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(identityEquipmentPacket);
                PacketPlayOutEntityEquipment potencyEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(potencyStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(potencyEquipmentPacket);
                PacketPlayOutEntityEquipment durationEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(durationStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(durationEquipmentPacket);
                PacketPlayOutEntityEquipment brewingTimeEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(brewingTimeStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(brewingTimeEquipmentPacket);
            }
        }.runTaskLater(PitSim.INSTANCE, 1);
    }




}
