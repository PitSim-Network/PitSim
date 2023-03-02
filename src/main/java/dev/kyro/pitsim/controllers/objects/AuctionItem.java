package dev.kyro.pitsim.controllers.objects;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.WinAuctionsQuest;
import dev.kyro.pitsim.controllers.AuctionDisplays;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.enums.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AuctionItem {
	public ItemType item;
	public int itemData;
	public int slot;
	public Map<UUID, Integer> bidMap;

	public static List<PluginMessage> waitingMessages = new ArrayList<>();

	public AuctionItem(ItemType item, int itemData, int slot, Map<UUID, Integer> bidMap) {
		this.item = item;
		this.itemData = itemData;
		this.slot = slot;

		this.bidMap = bidMap == null ? new LinkedHashMap<>() : bidMap;

		if(this.itemData == 0) {
			if(item.id == 5 || item.id == 6 || item.id == 7) {
				this.itemData = ItemType.generateJewelData(this.item.item);
			}
		}

		saveData();
	}

	public void saveData() {
		FirestoreManager.AUCTION.auctions.set(slot, new AuctionData.Auction());
		FirestoreManager.AUCTION.auctions.get(slot).item = this.item.id;
		FirestoreManager.AUCTION.auctions.get(slot).itemData = this.itemData;

		for(Map.Entry<UUID, Integer> entry : this.bidMap.entrySet()) {
			List<String> bids = FirestoreManager.AUCTION.auctions.get(slot).bids;

			for(String bid : bids) {
				String[] split = bid.split(":");
				if(split[0].equals(entry.getKey().toString())) {
					bids.remove(bid);
					break;
				}
			}

			bids.add(entry.getKey() + ":" + entry.getValue());
			FirestoreManager.AUCTION.auctions.get(slot).bids = bids;
		}
	}

	public void addBid(UUID player, int bid) {
		AuctionManager.sendAlert(player + " has bid " + bid + " souls on slot " + slot + " (" + item.itemName + ")");
		bidMap.put(player, bid);
		saveData();

		String bidPlayer = Bukkit.getOfflinePlayer(player).getName();

		PluginMessage message = new PluginMessage().writeString("AUCTION NOTIFY");
		message.writeInt(bid);
		message.writeString(bidPlayer);
		message.writeString(item.itemName);

		for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
			message.writeString(entry.getKey().toString());
		}

		message.send();
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
		AuctionManager.sendAlert("Auction " + slot + " has ended" + " (" + item.itemName + ")");
		FirestoreManager.AUCTION.auctions.set(slot, null);

		if(getHighestBidder() == null) {
			AuctionManager.sendAlert("Auction has no bidders");
			AuctionManager.sendAlert(bidMap.toString());
			return;
		}
		OfflinePlayer winner = Bukkit.getOfflinePlayer(getHighestBidder());

		if(winner.isOnline()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(winner.getPlayer());
			pitPlayer.stats.auctionsWon++;
			WinAuctionsQuest.INSTANCE.winAuction(pitPlayer);

			if(itemData == 0) {
				AUtil.giveItemSafely(winner.getPlayer(), item.item.clone(), true);
				AuctionManager.sendAlert("Given item to player");
			} else {
				ItemStack jewel = ItemType.getJewelItem(item.id, itemData);

				AUtil.giveItemSafely(winner.getPlayer(), jewel, true);
			}
			AuctionManager.sendAlert("Auction " + slot + " was claimed by " + winner.getName() + " since they were online (" + item.itemName + ")");

		} else {
			try {
				PluginMessage message = new PluginMessage().writeString("AUCTION ITEM REQUEST");
				message.writeString(winner.getName());
				message.writeString(PitSim.serverName);
				message.writeString(item.itemName);
				message.writeString(winner.getUniqueId().toString());
				message.writeInt(item.id);
				message.writeInt(itemData);
				message.writeInt(getHighestBid());
				message.send();

				waitingMessages.add(message);

				AuctionManager.sendAlert("Sent message to proxy");

			} catch(Exception e) {
				e.printStackTrace();
				AuctionManager.sendAlert("Auction " + slot + " failed to send message (" + item.itemName + ")");
			}
		}

		if(getHighestBidder() != null) bidMap.remove(getHighestBidder());

		for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());

			if(player.isOnline()) {
				PitPlayer.getPitPlayer(player.getPlayer()).taintedSouls += entry.getValue();
				AOutput.send(player.getPlayer(), "&5&lDARK AUCTION! &7Received &f" + entry.getValue() + " Tainted Souls&7.");
			} else {

				try {
					ApiFuture<DocumentSnapshot> data = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
							.document(player.getUniqueId().toString()).get();

					int soulReturn = data.get().get("soulReturn", Integer.class);
					soulReturn += entry.getValue();

					FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(player.getUniqueId().toString()).update("soulReturn", soulReturn);

				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			if(AuctionDisplays.pedestalItems[slot] != null && !AuctionDisplays.hasPlayers(AuctionDisplays.pedestalLocations[0]))
				AuctionDisplays.getItem(AuctionDisplays.pedestalItems[slot]).remove();
		} catch(Exception ignored) {}
	}
}
