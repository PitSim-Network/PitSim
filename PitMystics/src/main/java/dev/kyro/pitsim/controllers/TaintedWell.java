package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.*;
import dev.kyro.pitsim.*;
import org.bukkit.plugin.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import de.tr7zw.nbtapi.*;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.controllers.objects.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import dev.kyro.pitsim.tutorial.*;
import dev.kyro.pitsim.misc.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import java.util.*;
import net.minecraft.server.v1_8_R3.*;

public class TaintedWell implements Listener
{
    public static Location wellLocation;
    public static ArmorStand wellStand;
    public static ArmorStand textLine1;
    public static ArmorStand textLine2;
    public static ArmorStand textLine3;
    public static ArmorStand textLine4;
    public static Map<Player, ArmorStand> removeStands;
    public static Map<Player, ArmorStand> enchantStands;
    public static List<Player> enchantingPlayers;
    private static Map<Player, ItemStack> playerItems;
    public static int i;

    public static void onStart() {
        if(wellLocation == null) return;
        if(wellLocation.getChunk() == null) return;
        wellLocation.getChunk().load();
        (TaintedWell.wellStand = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.5, 0.5), (Class)ArmorStand.class)).setGravity(false);
        TaintedWell.wellStand.setArms(true);
        TaintedWell.wellStand.setVisible(false);
        (TaintedWell.textLine1 = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 1.0, 0.5), (Class)ArmorStand.class)).setGravity(false);
        TaintedWell.textLine1.setArms(true);
        TaintedWell.textLine1.setVisible(false);
        TaintedWell.textLine1.setCustomName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well");
        TaintedWell.textLine1.setCustomNameVisible(true);
        (TaintedWell.textLine2 = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.7, 0.5), (Class)ArmorStand.class)).setGravity(false);
        TaintedWell.textLine2.setArms(true);
        TaintedWell.textLine2.setVisible(false);
        TaintedWell.textLine2.setCustomName(ChatColor.GRAY + "Enchant Mystic Items found");
        TaintedWell.textLine2.setCustomNameVisible(true);
        (TaintedWell.textLine3 = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.4, 0.5), (Class)ArmorStand.class)).setGravity(false);
        TaintedWell.textLine3.setArms(true);
        TaintedWell.textLine3.setVisible(false);
        TaintedWell.textLine3.setCustomName(ChatColor.GRAY + "in the Darkzone here");
        TaintedWell.textLine3.setCustomNameVisible(true);
        (TaintedWell.textLine4 = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.1, 0.5), (Class)ArmorStand.class)).setGravity(false);
        TaintedWell.textLine4.setArms(true);
        TaintedWell.textLine4.setVisible(false);
        TaintedWell.textLine4.setCustomName(ChatColor.YELLOW + "Right-Click with an Item!");
        TaintedWell.textLine4.setCustomNameVisible(true);
        TaintedWell.wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
    }

    public static void onEnchant(final Player player, final ItemStack itemStack) {
        TaintedWell.playerItems.put(player, itemStack);
        player.getInventory().remove(itemStack);
        final PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(TaintedWell.wellStand), 0, CraftItemStack.asNMSCopy(itemStack));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        showButtons(player);
    }

    public static void showButtons(final Player player) {
        final ArmorStand removeStand = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.0, 0.5), (Class)ArmorStand.class);
        removeStand.setGravity(false);
        removeStand.setArms(true);
        removeStand.setVisible(false);
        removeStand.setCustomName(ChatColor.RED + "Remove Item");
        removeStand.setCustomNameVisible(true);
        TaintedWell.removeStands.put(player, removeStand);
        final ArmorStand enchantStand = (ArmorStand)TaintedWell.wellLocation.getWorld().spawn(TaintedWell.wellLocation.clone().add(0.5, 0.0, 0.5), (Class)ArmorStand.class);
        enchantStand.setGravity(false);
        enchantStand.setArms(true);
        enchantStand.setVisible(false);
        enchantStand.setCustomName(ChatColor.GREEN + "Enchant Item");
        enchantStand.setCustomNameVisible(true);
        TaintedWell.enchantStands.put(player, enchantStand);
        final PacketPlayOutEntityEquipment removePacket = new PacketPlayOutEntityEquipment(getStandID(removeStand), 4, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
        final PacketPlayOutEntityEquipment enchantPacket = new PacketPlayOutEntityEquipment(getStandID(enchantStand), 4, CraftItemStack.asNMSCopy(new ItemStack(new ItemStack(Material.EMERALD_BLOCK))));
        new BukkitRunnable() {
            public void run() {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(removePacket);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(enchantPacket);
            }
        }.runTaskLater(PitSim.INSTANCE, 1L);
        final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)removeStand).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
        final PacketPlayOutSpawnEntityLiving enchantSpawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)enchantStand).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(enchantSpawn);
        removeStand.teleport(removeStand.getLocation().clone().subtract(2.0, 0.0, 0.0));
        final PacketPlayOutEntityTeleport tpPacket = new PacketPlayOutEntityTeleport(((CraftEntity)removeStand).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpPacket);
        enchantStand.teleport(enchantStand.getLocation().clone().add(2.0, 0.0, 0.0));
        final PacketPlayOutEntityTeleport tpRemovePacket = new PacketPlayOutEntityTeleport(((CraftEntity)enchantStand).getHandle());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpRemovePacket);
        setText(player, "§c", "§c", "§c", "§c");
        setItemText(player);
    }

    public static void onButtonPush(final Player player, final boolean enchant) {
        final ArmorStand removeStand = TaintedWell.removeStands.get(player);
        final ArmorStand enchantStand = TaintedWell.enchantStands.get(player);
        final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(removeStand), (byte)64, (byte)0, (byte)0, (byte)0, (byte)0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpPacket);
        new BukkitRunnable() {
            public void run() {
                TaintedWell.removeStands.remove(player);
                removeStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 2L);
        final PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpRemovePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(enchantStand), (byte)(-64), (byte)0, (byte)0, (byte)0, (byte)0, false);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpRemovePacket);
        new BukkitRunnable() {
            public void run() {
                TaintedWell.enchantStands.remove(player);
                enchantStand.remove();
            }
        }.runTaskLater(PitSim.INSTANCE, 2L);
        if (!enchant) {
            TaintedWell.setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
            final ItemStack item = TaintedWell.playerItems.get(player);
            AUtil.giveItemSafely(player, item, true);
            TaintedWell.playerItems.remove(player);
            TaintedWell.enchantingPlayers.remove(player);
            final PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(TaintedWell.wellStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            new BukkitRunnable() {
                public void run() {
                    TaintedWell.setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
                }
            }.runTaskLater(PitSim.INSTANCE, 3L);
        }
        else {
            final NBTItem nbtFreshItem = new NBTItem(TaintedWell.playerItems.get(player));
            final int freshTier = nbtFreshItem.getInteger(NBTTag.TAINTED_TIER.getRef());
            if (freshTier == 3) {
                setText(player, "§c", ChatColor.RED + "Item is Max Tier!", ChatColor.RED + "Please remove", "§c");
                new BukkitRunnable() {
                    public void run() {
                        TaintedWell.setText(player, "§c", "§c", "§c", "§c");
                        TaintedWell.showButtons(player);
                    }
                }.runTaskLater(PitSim.INSTANCE, 40L);
                return;
            }


            ItemStack freshItem = TaintedWell.playerItems.get(player);

            try {
                final ItemStack newItem;
                if(MysticType.getMysticType(freshItem) == MysticType.TAINTED_SCYTHE) newItem = TaintedEnchanting.enchantScythe(freshItem, freshTier);
                else newItem = TaintedEnchanting.enchantChestplate(freshItem, freshTier);
                final NBTItem nbtItem = new NBTItem(newItem);
                if (nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef())) {
                    final int tier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
                    nbtItem.setInteger(NBTTag.TAINTED_TIER.getRef(), tier + 1);
                }
                else {
                    nbtItem.setInteger(NBTTag.TAINTED_TIER.getRef(), 1);
                }
                TaintedWell.playerItems.put(player, nbtItem.getItem());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            TaintedWell.enchantingPlayers.add(player);
            setText(player, "§c", "§c", "§c", ChatColor.YELLOW + "Its rolling...");
            new BukkitRunnable() {
                public void run() {
                    TaintedWell.enchantingPlayers.remove(player);
                    player.playEffect(TaintedWell.wellLocation.clone().add(0.0, 1.0, 0.0), Effect.EXPLOSION_HUGE, 0);
                    Sounds.EXPLOSIVE_3.play(player);
                    TaintedWell.showButtons(player);
                }
            }.runTaskLater(PitSim.INSTANCE, 80L);
        }
    }

    @EventHandler
    public static void onEnchantingTableClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        if (block.getType() != Material.ENCHANTMENT_TABLE) {
            return;
        }
        if (player.getWorld() != Bukkit.getWorld("darkzone")) {
            return;
        }
        event.setCancelled(true);
        if (TaintedWell.playerItems.containsKey(event.getPlayer())) {
            return;
        }
        if (TutorialManager.tutorials.containsKey(event.getPlayer())) {
            return;
        }
        if (Misc.isAirOrNull(player.getItemInHand())) {
            return;
        }
        final NBTItem nbtItem = new NBTItem(player.getItemInHand());
        if (!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) || MysticType.getMysticType(player.getItemInHand()) == MysticType.SWORD || MysticType.getMysticType(player.getItemInHand()) == MysticType.BOW || MysticType.getMysticType(player.getItemInHand()) == MysticType.PANTS) {
            setText(player, "§c", ChatColor.RED + "Invalid Item!", ChatColor.RED + "Please try again!", "§c");
            new BukkitRunnable() {
                public void run() {
                    if (!TaintedWell.playerItems.containsKey(player)) {
                        TaintedWell.setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
                    }
                }
            }.runTaskLater(PitSim.INSTANCE, 40L);
            return;
        }
        onEnchant(player, player.getItemInHand());
        Sounds.MYSTIC_WELL_OPEN_1.play(player);
        Sounds.MYSTIC_WELL_OPEN_2.play(player);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        if (TaintedWell.playerItems.containsKey(event.getPlayer())) {
            AUtil.giveItemSafely(event.getPlayer(), TaintedWell.playerItems.get(event.getPlayer()));
        }
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (!TaintedWell.playerItems.containsKey(event.getPlayer())) {
            return;
        }
        if (event.getPlayer().getWorld() != Bukkit.getWorld("darkzone")) {
            onButtonPush(event.getPlayer(), false);
        }
        if (event.getPlayer().getLocation().distance(TaintedWell.wellLocation) > 10.0) {
            onButtonPush(event.getPlayer(), false);
        }
    }

    @EventHandler
    public void onStandClick(final PlayerInteractAtEntityEvent event) {
        if (!TaintedWell.playerItems.containsKey(event.getPlayer())) {
            return;
        }
        for (final ArmorStand value : TaintedWell.removeStands.values()) {
            if (value.getUniqueId().equals(event.getRightClicked().getUniqueId())) {
                onButtonPush(event.getPlayer(), false);
            }
        }
        for (final ArmorStand value : TaintedWell.enchantStands.values()) {
            if (value.getUniqueId().equals(event.getRightClicked().getUniqueId())) {
                onButtonPush(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onHit(AttackEvent.Pre event) {
        if(event.defender.getUniqueId().equals(wellStand.getUniqueId())) event.setCancelled(true);
        if(event.defender.getUniqueId().equals(textLine1.getUniqueId())) event.setCancelled(true);
        if(event.defender.getUniqueId().equals(textLine2.getUniqueId())) event.setCancelled(true);
        if(event.defender.getUniqueId().equals(textLine3.getUniqueId())) event.setCancelled(true);
        if(event.defender.getUniqueId().equals(textLine4.getUniqueId())) event.setCancelled(true);

        for (ArmorStand value : enchantStands.values()) {
            if(value.getUniqueId().equals(event.defender.getUniqueId())) event.setCancelled(true);
        }
        for (ArmorStand value : removeStands.values()) {
            if(value.getUniqueId().equals(event.defender.getUniqueId())) event.setCancelled(true);
        }

    }

    public static int getStandID(final ArmorStand stand) {
        for (final Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(TaintedWell.wellLocation, 5.0, 5.0, 5.0)) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            if (entity.getUniqueId().equals(stand.getUniqueId())) {
                return entity.getEntityId();
            }
        }
        return 0;
    }

    public static void setText(final Player player, final String line1, final String line2, final String line3, final String line4) {
        if (line1 != null) {
            final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)TaintedWell.textLine1).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
            final DataWatcher dw = ((CraftEntity)TaintedWell.textLine1).getHandle().getDataWatcher();
            dw.watch(2, (Object)line1);
            final PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(TaintedWell.textLine1), dw, false);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
        }
        else {
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(TaintedWell.textLine1));
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
        }
        if (line2 != null) {
            final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)TaintedWell.textLine2).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
            final DataWatcher dw = ((CraftEntity)TaintedWell.textLine2).getHandle().getDataWatcher();
            dw.watch(2, (Object)line2);
            final PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(TaintedWell.textLine2), dw, false);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
        }
        else {
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(TaintedWell.textLine2));
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
        }
        if (line3 != null) {
            final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)TaintedWell.textLine3).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
            final DataWatcher dw = ((CraftEntity)TaintedWell.textLine3).getHandle().getDataWatcher();
            dw.watch(2, (Object)line3);
            final PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(TaintedWell.textLine3), dw, false);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
        }
        else {
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(TaintedWell.textLine3));
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
        }
        if (line4 != null) {
            final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)TaintedWell.textLine4).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
            final DataWatcher dw = ((CraftEntity)TaintedWell.textLine4).getHandle().getDataWatcher();
            dw.watch(2, (Object)line4);
            final PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(TaintedWell.textLine4), dw, false);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
        }
        else {
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(TaintedWell.textLine4));
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
        }
    }

    public static void setItemText(final Player player) {
        final ItemStack item = TaintedWell.playerItems.get(player);
        final Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(item);
        final List<PitEnchant> enchants = new ArrayList<PitEnchant>(enchantMap.keySet());
        if (enchants.size() == 0) {
            setText(player, item.getItemMeta().getDisplayName(), "§c", "§c", "§c");
            return;
        }
        String enchant1 = "§c";
        String enchant2 = "§c";
        String enchant3 = "§c";
        enchant1 = enchants.get(0).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(0)));
        if (enchants.size() > 1) {
            enchant2 = enchants.get(1).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(1)));
        }
        if (enchants.size() > 2) {
            enchant3 = enchants.get(2).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(2)));
        }
        setText(player, item.getItemMeta().getDisplayName(), enchant1, enchant2, enchant3);
    }

    static {
        TaintedWell.wellLocation = new Location(Bukkit.getWorld("darkzone"), 199.0, 92.0, -115.0);
        TaintedWell.removeStands = new HashMap<>();
        TaintedWell.enchantStands = new HashMap<>();
        TaintedWell.enchantingPlayers = new ArrayList<>();
        TaintedWell.playerItems = new HashMap<>();
        TaintedWell.i = 0;
        new BukkitRunnable() {
            public void run() {
                if(TaintedWell.wellStand != null){
                    for (Entity entity : TaintedWell.wellStand.getNearbyEntities(25.0, 25.0, 25.0)) {
                        if (!(entity instanceof Player)) {
                            continue;
                        }
                        Player player = (Player)entity;

                        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(TaintedWell.getStandID(TaintedWell.wellStand), (byte)0, (byte)0, (byte)0, (byte)TaintedWell.i, (byte)0, false);
                        EntityPlayer nmsPlayer = ((CraftPlayer)entity).getHandle();
                        nmsPlayer.playerConnection.sendPacket(packet);
                        for (Map.Entry<Player, ArmorStand> entry : TaintedWell.enchantStands.entrySet()) {
                            if (player == entry.getKey()) {
                                continue;
                            }
                            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(TaintedWell.getStandID(entry.getValue()));
                            nmsPlayer.playerConnection.sendPacket(destroyPacket);
                        }
                        for (Map.Entry<Player, ArmorStand> entry : TaintedWell.removeStands.entrySet()) {
                            if (player == entry.getKey()) {
                                continue;
                            }
                            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(TaintedWell.getStandID(entry.getValue()));
                            nmsPlayer.playerConnection.sendPacket(destroyPacket);
                        }
                        if (!TaintedWell.playerItems.containsKey(player)) {}
                        if (TaintedWell.enchantingPlayers.contains(player)) {
                            TaintedWell.i += 24;
                            player.playEffect(TaintedWell.wellLocation.clone().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 0);
                        }
                        else {
                            TaintedWell.i += 8;
                        }
                        if (TaintedWell.i < 256) {
                            continue;
                        }
                        TaintedWell.i = 0;
                    }
                }

            }
        }.runTaskTimer(PitSim.INSTANCE, 2L, 2L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : TaintedWell.wellStand.getNearbyEntities(25.0, 25.0, 25.0)) {
                    if(!(entity instanceof Player)) {
                        continue;
                    }
                    Player player = (Player) entity;
                    if(!enchantingPlayers.contains(player) && !removeStands.containsKey(player))
                        TaintedWell.setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 100, 100);
    }
}
