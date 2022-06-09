package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.inventories.BidGUI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionDisplays implements Listener {

    public static Location[] pedestalLocations = new Location[3];
    public static UUID[] pedestalItems = new UUID[3];
    public static UUID[] pedestalArmorStands = new UUID[3];

    public static UUID[] highestBidStands = new UUID[3];
    public static UUID[] highestBidderStands = new UUID[3];
    public static UUID[] rightClickStands = new UUID[3];

    public static UUID timerStandUUID;

    public static NPC[] clickables = new NPC[3];

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {

                    Item item = getItem(pedestalItems[i]);
                    item.teleport(pedestalLocations[i]);

                    for (Entity nearbyEntity : MapManager.getDarkzone().getNearbyEntities(pedestalLocations[i], 1, 1, 1)) {
                        if(!(nearbyEntity instanceof Item)) continue;
                        if(nearbyEntity.getUniqueId().equals(pedestalItems[i])) continue;
                        nearbyEntity.remove();
                    }

                    int highestBid = AuctionManager.auctionItems[i].getHighestBid();
                    UUID highestBidder = AuctionManager.auctionItems[i].getHighestBidder();

                    ArmorStand highestBidStand = getStand(highestBidStands[i]);
                    if(highestBidder != null) highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");
                    else highestBidStand.setCustomName(ChatColor.YELLOW + "Starting Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");

                    String message = highestBidder == null ? "No One!" : Bukkit.getOfflinePlayer(highestBidder).getName();
                    ArmorStand highestBidderStand = getStand(highestBidderStands[i]);
                    highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD + message);

                }

                for (int i = 0; i < clickables.length; i++) {
                    NPC clickable = clickables[i];

                    clickable.spawn(pedestalLocations[i]);
                    clickable.teleport(pedestalLocations[i], PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    if(clickable.isSpawned()) ((LivingEntity) clickable.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                }


                getStand(timerStandUUID).setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + getRemainingTime());
            }
        }.runTaskTimer(PitSim.INSTANCE, 20, 20);
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

            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            NPC npc = registry.createNPC(EntityType.MAGMA_CUBE, "");
            npc.spawn(pedestalLocations[i]);
            clickables[i] = npc;

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
    public void onRightClick(NPCRightClickEvent event) {
        for (int i = 0; i < clickables.length; i++) {
            NPC clickable = clickables[i];

            if(clickable.getId() == event.getNPC().getId()) {
                BidGUI bidGUI = new BidGUI(event.getClicker(), i);
                bidGUI.open();
            }
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

    public static String getRemainingTime() {
        return convertSecondsToHMmSs((AuctionManager.minutes * 60000L - (System.currentTimeMillis() - AuctionManager.auctionItems[0].initTime)) / 1000);
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%dh %02dm %02ds", h,m,s);
    }

}
