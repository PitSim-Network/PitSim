package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.controllers.AuctionDisplays;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.enums.NBTTag;
import javafx.util.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionItem {

    public ItemType item;
    public int itemData;
    public int slot;
    public long initTime;
    public Map<UUID, Integer> bidMap;

    public AuctionItem(ItemType item, int itemData, int slot, long initTime, Map<UUID, Integer> bidMap) {
       this.item = item;
       this.itemData = itemData;
       this.slot = slot;
       this.initTime = initTime == 0 ? System.currentTimeMillis() : initTime;

       this.bidMap = bidMap == null ? new HashMap<>() : bidMap;

       NBTItem nbtItem = new NBTItem(this.item.item);
       if(this.itemData == 0 && nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) this.itemData = ItemType.generateJewelData(this.item.item);

       saveData();
    }

    public void saveData() {
        AConfig.set("auctions.auction" + slot + ".item", this.item.id);
        AConfig.set("auctions.auction" + slot + ".itemdata", this.itemData);
        AConfig.set("auctions.auction" + slot + ".start", this.initTime);

        for (Map.Entry<UUID, Integer> entry : this.bidMap.entrySet()) {
            List<String> bids = AConfig.getStringList("auctions.auction" + slot + ".bids");

            for (String bid : bids) {
                String[] split = bid.split(":");
                if(split[0].equals(entry.getKey().toString())) {
                    bids.remove(bid);
                    break;
                }
            }

            bids.add(entry.getKey() + ":" + entry.getValue());
            AConfig.set("auctions.auction" + slot + ".bids", bids);
        }
        AConfig.saveConfig();
    }

    public void addBid(UUID player, int bid) {
        bidMap.put(player, bid);
        saveData();
    }

    public int getHighestBid() {
        int highest = 0;
        for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
            if(entry.getValue() > highest) {
                highest = entry.getValue();
            }
        }
        return highest;
    }

    public UUID getHighestBidder() {
        int highest = 0;
        UUID player = null;
        for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
            if(entry.getValue() > highest) {
                highest = entry.getValue();
                player = entry.getKey();
            }
        }
        return player;
    }

    public int getBid(UUID player) {
        return bidMap.getOrDefault(player, 0);
    }

    public void endAuction() {
        AConfig.set("auctions.auction" + slot + ".item", (Object) null);
        AConfig.set("auctions.auction" + slot + ".itemdata", (Object) null);
        AConfig.set("auctions.auction" + slot + ".start", (Object) null);
        AConfig.set("auctions.auction" + slot + ".bids", (Object) null);

        AConfig.set("auctions.auction" + slot, (Object) null);

        AConfig.saveConfig();

        AuctionDisplays.getItem(AuctionDisplays.pedestalItems[slot]).remove();
    }
}
