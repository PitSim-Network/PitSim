package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuctionDisplays implements Listener {

    public static Location[] pedestalLocations = new Location[3];
    public static UUID[] pedestalItems = new UUID[3];
    public static UUID[] pedestalArmorStands = new UUID[3];

    public static UUID[] highestBidStands = new UUID[3];
    public static UUID[] highestBidderStands = new UUID[3];
    public static UUID[] rightClickStands = new UUID[3];

    public static UUID timerStandUUID;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Bukkit.getWorld("darkzone").getPlayers().size() == 0) return;
                System.out.println(Bukkit.getWorld("darkzone").getPlayers().size());
                for (int i = 0; i < 3; i++) {

                    Item item = getItem(pedestalItems[i]);
                    item.teleport(pedestalLocations[i]);

                    int highestBid = AuctionManager.auctionItems[i].getHighestBid();
                    ArmorStand highestBidStand = getStand(highestBidStands[i]);
                    highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");

                    UUID highestBidder = AuctionManager.auctionItems[i].getHighestBidder();
                    String message = highestBidder == null ? "No One!" : Bukkit.getOfflinePlayer(highestBidder).getName();
                    ArmorStand highestBidderStand = getStand(highestBidderStands[i]);
                    highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD + message);

                }

                int timeLeft = (int) ((System.currentTimeMillis() - AuctionManager.auctionItems[0].initTime) / 60000L);

                getStand(timerStandUUID).setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + (AuctionManager.minutes - timeLeft) + "m");
            }
        }.runTaskTimer(PitSim.INSTANCE, 60, 60);
    }

    public static void onStart() {
        pedestalLocations[0] = new Location(MapManager.getDarkzone(), 237.5, 83, -292.5);
        pedestalLocations[1] = new Location(MapManager.getDarkzone(), 243.5, 83, -295.5);
        pedestalLocations[2] = new Location(MapManager.getDarkzone(), 249.5, 83, -292.5);

        ArmorStand timerStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(new Location(MapManager.getDarkzone(), 243.5, 81, -289.5), EntityType.ARMOR_STAND);
        timerStand.setGravity(false);
        timerStand.setVisible(false);
        timerStand.setCustomNameVisible(true);
//        timerStand.setRemoveWhenFarAway(false);
        timerStand.setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + "0m");
        timerStandUUID = timerStand.getUniqueId();

        for (int i = 0; i < 3; i++) {
            ArmorStand highestBidStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
            highestBidStand.setVisible(false);
            highestBidStand.setCustomNameVisible(true);
            highestBidStand.setGravity(false);
//            highestBidStand.setRemoveWhenFarAway(false);

            highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + " Tainted Souls");
            highestBidStands[i] = highestBidStand.getUniqueId();

            ArmorStand highestBidderStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.3, 0), EntityType.ARMOR_STAND);
            highestBidderStand.setVisible(false);
            highestBidderStand.setCustomNameVisible(true);
            highestBidderStand.setGravity(false);
//            highestBidderStand.setRemoveWhenFarAway(false);

            highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD);
            highestBidderStands[i] = highestBidderStand.getUniqueId();

            ArmorStand rightClickStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0, 0), EntityType.ARMOR_STAND);
            rightClickStand.setVisible(false);
            rightClickStand.setCustomNameVisible(true);
            rightClickStand.setGravity(false);
            rightClickStand.setCustomName(ChatColor.YELLOW + "Right-Click to Bid!");
//            rightClickStand.setRemoveWhenFarAway(false);
            rightClickStands[i] = rightClickStand.getUniqueId();
        }
    }

    public static void showItems() {
        pedestalLocations[0] = new Location(MapManager.getDarkzone(), 237.5, 83, -292.5);
        pedestalLocations[1] = new Location(MapManager.getDarkzone(), 243.5, 83, -295.5);
        pedestalLocations[2] = new Location(MapManager.getDarkzone(), 249.5, 83, -292.5);
        for (Location pedestalLocation : pedestalLocations) {
            pedestalLocation.getChunk().load();
        }

        for (int i = 0; i < pedestalLocations.length; i++) {
            Location pedestalLocation = pedestalLocations[i];
            ItemStack dropItem = AuctionManager.auctionItems[i].item.item.clone();
            ItemMeta meta = dropItem.getItemMeta();
            meta.setDisplayName(UUID.randomUUID().toString());
            dropItem.setItemMeta(meta);

            pedestalItems[i] = pedestalLocation.getWorld().dropItem(pedestalLocation, dropItem).getUniqueId();

            ArmorStand stand = (ArmorStand) pedestalLocation.getWorld().spawnEntity(pedestalLocation.clone().subtract(0, 1.33, 0), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setHelmet(new ItemStack(Material.GLASS));
            stand.setCustomName(AuctionManager.auctionItems[i].item.itemName);
            stand.setRemoveWhenFarAway(false);
            stand.setCustomNameVisible(true);
            pedestalArmorStands[i] = stand.getUniqueId();

        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        for (UUID pedestalItem : pedestalItems) {
            if(pedestalItem.equals(event.getItem().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        for (UUID pedestalItem : pedestalItems) {
            if(pedestalItem.equals(event.getEntity().getUniqueId())) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(AttackEvent.Pre event) {
        List<UUID> stands = new ArrayList<>();

        stands.addAll(Arrays.asList(pedestalArmorStands));
        stands.addAll(Arrays.asList(highestBidderStands));
        stands.addAll(Arrays.asList(highestBidStands));
        stands.addAll(Arrays.asList(rightClickStands));
        stands.add(timerStandUUID);


        for (UUID armorStand : stands) {
            if(armorStand.equals(event.defender.getUniqueId())) {
                event.setCancelled(true);
                event.event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        for (UUID armorStand : pedestalArmorStands) {
            if(armorStand.equals(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    public static ArmorStand getStand(UUID uuid) {
        for (Entity entity : MapManager.getDarkzone().getEntities()) {
            if(!(entity instanceof ArmorStand)) continue;
            if(entity.getUniqueId().equals(uuid)) return (ArmorStand) entity;
        }
        return null;
    }

    public static Item getItem(UUID uuid) {
        for (Entity entity : MapManager.getDarkzone().getEntities()) {
            if(!(entity instanceof Item)) continue;
            if(entity.getUniqueId().equals(uuid)) return (Item) entity;
        }
        return null;
    }

}
