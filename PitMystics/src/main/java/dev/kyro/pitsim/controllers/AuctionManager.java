package dev.kyro.pitsim.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionData;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class AuctionManager implements Listener {

    public static AuctionItem[] auctionItems = new AuctionItem[3];

    public static Location spawnLoc = new Location(MapManager.getDarkzone(), 178.5, 52, -1004.5, 180, 0);
    public static Location returnLoc = new Location(MapManager.getDarkzone(), 255.5, 91, -134.5, -45, 0);

    public static int minutes = 1;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!PitSim.isDarkzone()) return;

                boolean showItems = false;

                for (int i = 0; i < auctionItems.length; i++) {
                    AuctionItem item = auctionItems[i];

                    if(System.currentTimeMillis() - item.initTime > minutes * 60000L) {
                        item.endAuction();
                        System.out.println(i + " Auction ended");
                        auctionItems[i] = new AuctionItem(generateItem(), 0, i, 0, null);
                        showItems = true;
                    }
                }

                if(showItems) AuctionDisplays.showItems();

            }
        }.runTaskTimer(PitSim.INSTANCE, 10, 10);
    }

    public static void onStart() {

        for (int i = 0; i < 3; i++) {
            if(FirestoreManager.AUCTION.auctions.get(i) == null) continue;

            int item = FirestoreManager.AUCTION.auctions.get(i).item;
            int itemData = FirestoreManager.AUCTION.auctions.get(i).itemData;
            long startTime = FirestoreManager.AUCTION.auctions.get(i).start;

            List<String> bids = FirestoreManager.AUCTION.auctions.get(i).bids;
            Map<UUID, Integer> bidMap = new LinkedHashMap<>();
            for (String bid : bids) {
                String[] split = bid.split(":");
                bidMap.put(UUID.fromString(split[0]), Integer.parseInt(split[1]));
            }

            System.out.println("Loaded auction " + i);
            System.out.println(item);
            auctionItems[i] = new AuctionItem(ItemType.getItemType(item), itemData, i, startTime, bidMap);
        }

        for (int i = 0; i < auctionItems.length; i++) {
            if(auctionItems[i] != null) continue;

            auctionItems[i] = new AuctionItem(generateItem(), 0, i, 0, null);
        }

        AuctionDisplays.showItems();
    }

    public static ItemType generateItem() {
        double random = Math.random() * 100;

        List<ItemType> itemTypes = Arrays.asList(ItemType.values());
        Collections.shuffle(itemTypes);

        for (ItemType itemType : itemTypes) {
            if(itemType.chance > random) return itemType;
        }

        return itemTypes.get(0);
    }

    List<Player> anim = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if(anim.contains(player)) return;
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionManager regions = container.get(event.getTo().getWorld());
        assert regions != null;
        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(event.getTo())));

        for(ProtectedRegion region : set) {
            if(region.getId().equals("darkauctionenterance")) {

                anim.add(player);
                Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 60, 99, false, false);
                Misc.applyPotionEffect(player, PotionEffectType.CONFUSION, 60, 5, false, false);
                Sounds.MANA.play(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        anim.remove(player);
                    }
                }.runTaskLater(PitSim.INSTANCE, 20);
            }

            if(region.getId().equals("darkauctionexit")) {

                anim.add(player);
                Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 60, 99, false, false);
                Misc.applyPotionEffect(player, PotionEffectType.CONFUSION, 60, 5, false, false);
                Sounds.MANA.play(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(returnLoc);
                        anim.remove(player);
                    }
                }.runTaskLater(PitSim.INSTANCE, 20);
            }
        }
    }

    @EventHandler
    public static void onMessageReceived(MessageEvent event) throws ExecutionException, InterruptedException {

        PluginMessage initialMessage = null;

        for(PluginMessage waitingMessage : AuctionItem.waitingMessages) {
            if(event.getMessage().isResponseTo(waitingMessage)) initialMessage = waitingMessage;
        }

        if(initialMessage == null) return;

        boolean response = event.getMessage().getBooleans().get(0);
        if(!response) {
            PitPlayer pitPlayer = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
                    .document(initialMessage.getStrings().get(4)).get().get().toObject(PitPlayer.class);

            assert pitPlayer != null;
            List<Integer> ints = initialMessage.getIntegers();
            System.out.println(pitPlayer);
            System.out.println(ints);
            pitPlayer.auctionReturn.add(ints.get(0) + ":" + ints.get(1) + ":" + ints.get(2));

            FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(initialMessage.getStrings().get(4)).set(pitPlayer);
        }

        PluginMessage finalInitialMessage = initialMessage;
        new BukkitRunnable() {
            @Override
            public void run() {
                AuctionItem.waitingMessages.remove(finalInitialMessage);
            }
        }.runTaskLater(PitSim.INSTANCE, 1);

    }


}
