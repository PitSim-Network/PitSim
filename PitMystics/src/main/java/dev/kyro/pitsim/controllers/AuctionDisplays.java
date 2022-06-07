package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.events.AttackEvent;
import javafx.util.Pair;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuctionDisplays implements Listener {

    public static Location[] pedestalLocations = new Location[3];
    public static Item[] pedestalItems = new Item[3];
    public static ArmorStand[] pedestalArmorStands = new ArmorStand[3];

    public static ArmorStand[] highestBidStands = new ArmorStand[3];
    public static ArmorStand[] highestBidderStands = new ArmorStand[3];
    public static ArmorStand[] rightClickStands = new ArmorStand[3];

    public static ArmorStand timerStand;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < pedestalItems.length; i++) {
                    pedestalItems[i].teleport(pedestalLocations[i]);

                    int highestBid = AuctionManager.auctionItems[i].getHighestBid();
                    ArmorStand highestBidStand = highestBidStands[i];
                    highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");

                    UUID highestBidder = AuctionManager.auctionItems[i].getHighestBidder();
                    String message = highestBidder == null ? "No one!" : Bukkit.getOfflinePlayer(highestBidder).getName();
                    highestBidderStands[i].setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD + message);
                }

                int timeLeft = (int) ((System.currentTimeMillis() - AuctionManager.auctionItems[0].initTime) / 60000L);

                timerStand.setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + (AuctionManager.minutes - timeLeft) + "m");
            }
        }.runTaskTimer(PitSim.INSTANCE, 60, 60);
    }

    public static void onStart() {
        pedestalLocations[0] = new Location(MapManager.getDarkzone(), 237.5, 83, -292.5);
        pedestalLocations[1] = new Location(MapManager.getDarkzone(), 243.5, 83, -295.5);
        pedestalLocations[2] = new Location(MapManager.getDarkzone(), 249.5, 83, -292.5);

        timerStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(new Location(MapManager.getDarkzone(), 243.5, 81, -289.5), EntityType.ARMOR_STAND);
        timerStand.setGravity(false);
        timerStand.setVisible(false);
        timerStand.setCustomNameVisible(true);
        timerStand.setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + "0m");

        for (int i = 0; i < 3; i++) {
            ArmorStand highestBidStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
            highestBidStand.setVisible(false);
            highestBidStand.setCustomNameVisible(true);
            highestBidStand.setGravity(false);

            highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + " Tainted Souls");
            highestBidStands[i] = highestBidStand;

            ArmorStand highestBidderStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.3, 0), EntityType.ARMOR_STAND);
            highestBidderStand.setVisible(false);
            highestBidderStand.setCustomNameVisible(true);
            highestBidderStand.setGravity(false);

            highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD);
            highestBidderStands[i] = highestBidderStand;

            ArmorStand rightClickStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0, 0), EntityType.ARMOR_STAND);
            rightClickStand.setVisible(false);
            rightClickStand.setCustomNameVisible(true);
            rightClickStand.setGravity(false);
            rightClickStand.setCustomName(ChatColor.YELLOW + "Right-Click to Bid!");
            rightClickStands[i] = rightClickStand;
        }
    }

    public static void showItems() {
        pedestalLocations[0] = new Location(MapManager.getDarkzone(), 237.5, 83, -292.5);
        pedestalLocations[1] = new Location(MapManager.getDarkzone(), 243.5, 83, -295.5);
        pedestalLocations[2] = new Location(MapManager.getDarkzone(), 249.5, 83, -292.5);

        for (int i = 0; i < pedestalLocations.length; i++) {
            Location pedestalLocation = pedestalLocations[i];
            pedestalItems[i] = pedestalLocation.getWorld().dropItem(pedestalLocation, AuctionManager.auctionItems[i].item.item.clone());

            ArmorStand stand = (ArmorStand) pedestalLocation.getWorld().spawnEntity(pedestalLocation.clone().subtract(0, 1.33, 0), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setHelmet(new ItemStack(Material.GLASS));
            stand.setCustomName(AuctionManager.auctionItems[i].item.itemName);
            stand.setCustomNameVisible(true);
            pedestalArmorStands[i] = stand;

        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        for (Item pedestalItem : pedestalItems) {
            if(pedestalItem.getUniqueId().equals(event.getItem().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        for (Item pedestalItem : pedestalItems) {
            if(pedestalItem == event.getEntity()) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(AttackEvent.Pre event) {
        List<ArmorStand> stands = new ArrayList<>();

        stands.addAll(Arrays.asList(pedestalArmorStands));
        stands.addAll(Arrays.asList(highestBidderStands));
        stands.addAll(Arrays.asList(highestBidStands));
        stands.addAll(Arrays.asList(rightClickStands));
        stands.add(timerStand);


        for (ArmorStand armorStand : stands) {
            if(armorStand == event.defender) {
                event.setCancelled(true);
                event.event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        for (ArmorStand armorStand : pedestalArmorStands) {
            if(armorStand == event.getEntity()) {
                event.setCancelled(true);
            }
        }
    }

}
