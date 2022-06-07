package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.enums.ItemType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionManager {

    public static AuctionItem[] auctionItems = new AuctionItem[3];

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < auctionItems.length; i++) {
                    AuctionItem item = auctionItems[i];

                    if(System.currentTimeMillis() - item.initTime > 60 * 1000) {
                        item.endAuction();
                        System.out.println(i + " Auction ended");
                        auctionItems[i] = new AuctionItem(generateItem(), 0, i, 0, null);
                    }
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 20 * 60, 20 * 60);
    }

    public static void onStart() {

        for (int i = 0; i < 3; i++) {
            if(AConfig.getDouble("auctions." + i + ".item") == 0) continue;

            int item = AConfig.getInt("auctions.auction" + i + ".item");
            int itemData = AConfig.getInt("auctions.auction" + i + ".itemdata");
            long startTime = (long) AConfig.getDouble("auctions.auction" + i + ".start");

            List<String> bids = AConfig.getStringList("auctions.auction" + i + ".bids");
            Map<UUID, Integer> bidMap = new HashMap<>();
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


}
