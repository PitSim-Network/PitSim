package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.AuctionDisplays;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
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

       this.bidMap = bidMap == null ? new LinkedHashMap<>() : bidMap;

       NBTItem nbtItem = new NBTItem(this.item.item);
       if(this.itemData == 0) {
           if(item.id == 5 || item.id == 6 || item.id == 7) {
               this.itemData = ItemType.generateJewelData(this.item.item);
           }
       }

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

        String bidPlayer = Bukkit.getOfflinePlayer(player).getName();

        for (Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
            Player onlinePlayer = Bukkit.getOfflinePlayer(entry.getKey()).getPlayer();
            if(onlinePlayer == null || !onlinePlayer.isOnline()) continue;

            AOutput.send(onlinePlayer, "&5&lDARK AUCTION! &e" + bidPlayer + " &7bid &f" + bid + " Souls &7on " + item.itemName);
            Sounds.BOOSTER_REMIND.play(onlinePlayer);
        }

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
        return highest == 0 ? item.startingBid : highest;
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

        if(getHighestBidder() == null) return;

        OfflinePlayer winner = Bukkit.getOfflinePlayer(getHighestBidder());

        for (Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
            Player onlinePlayer = Bukkit.getOfflinePlayer(entry.getKey()).getPlayer();
            if(onlinePlayer != null && !onlinePlayer.isOnline()) continue;

            AOutput.send(onlinePlayer, "&5&lDARK AUCTION! &e" + winner.getName() + " &7won " + item.itemName + " &7for &f" + getHighestBid() + " Souls&7.");
            Sounds.BOOSTER_REMIND.play(onlinePlayer);
        }

        if(winner.isOnline()) {

            PitPlayer.getPitPlayer(winner.getPlayer()).stats.auctionsWon++;

            if(itemData == 0) {
                AUtil.giveItemSafely(winner.getPlayer(), item.item.clone(), true);
            } else {
                ItemStack jewel = ItemType.getJewelItem(item.id, itemData);

                AUtil.giveItemSafely(winner.getPlayer(), jewel, true);
            }

            AOutput.send(winner.getPlayer(), "&5&lDARK AUCTION! &7Received " + item.itemName + "&7.");
        } else {
            APlayer aPlayer = APlayerData.getPlayerData(winner);
            FileConfiguration playerData = aPlayer.playerData;

            if(playerData.contains("auctionreturn")) {
                String returnData = playerData.getString("auctionreturn");
                returnData += "," + item.id + ":" + itemData;
                playerData.set("auctionreturn", returnData);
            }
            else playerData.set("auctionreturn", item.id + ":" + itemData);

            aPlayer.save();
        }

        if(getHighestBidder() != null) bidMap.remove(getHighestBidder());

        for (Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {

            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());

            APlayer soulReturnPlayer = APlayerData.getPlayerData(player);
            FileConfiguration soulReturnPlayerData = soulReturnPlayer.playerData;

            if(player.isOnline()) {
                PitPlayer.getPitPlayer(player.getPlayer()).taintedSouls += entry.getValue();
                AOutput.send(player.getPlayer(), "&5&lDARK AUCTION! &7Received &f" + entry.getValue() + " Tainted Souls&7.");
            } else {
                if(soulReturnPlayerData.contains("soulreturn")) {
                    int soulReturn = soulReturnPlayerData.getInt("soulreturn");
                    soulReturn += entry.getValue();

                    soulReturnPlayerData.set("soulreturn", soulReturn);
                } else soulReturnPlayerData.set("soulreturn", entry.getValue());
                soulReturnPlayer.save();
            }
        }

        try {
            if(AuctionDisplays.pedestalItems[slot] != null && !AuctionDisplays.hasPlayers(AuctionDisplays.pedestalLocations[0])) AuctionDisplays.getItem(AuctionDisplays.pedestalItems[slot]).remove();
        } catch(Exception ignored) { }
    }
}
