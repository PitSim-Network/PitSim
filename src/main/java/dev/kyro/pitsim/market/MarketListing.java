package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class MarketListing implements Serializable {

	public UUID marketUUID;
	public UUID ownerUUID;
	public long creationTime;

	public long listingLength;

	public Map<UUID, Integer> bidMap;
	public ItemStack itemData;

	//Auction Data; -1 = Auction disabled
	public int startingBid;
	//Bin Data; -1 = Bin disabled
	public int binPrice;
	//Weather or not multiple items are being sold; Auction mode disabled by default if true
	public boolean stackBIN;

	public int claimableSouls;
	public boolean itemClaimed;
	public boolean hasEnded;

	public int originalStock;
	public String buyerDisplayName;
	public UUID buyer;

	public String ownerDisplayName;
	public Map<UUID, String> bidderDisplayNames = new HashMap<>();

	public MarketListing(PluginMessage message) {
		bidMap = new HashMap<>();
		updateListing(message);
	}

	public void updateListing(PluginMessage message) {
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();
		List<Long> longs = message.getLongs();

		marketUUID = UUID.fromString(strings.get(1));
		ownerUUID = UUID.fromString(strings.get(2));
		startingBid = ints.get(0);
		binPrice = ints.get(1);
		stackBIN = booleans.get(0);
		itemData = StorageProfile.deserialize(strings.get(3));
		listingLength = longs.get(0);
		creationTime = longs.get(1);

		claimableSouls = ints.get(2);
		itemClaimed = booleans.get(1);
		hasEnded = booleans.get(2);

		ownerDisplayName = strings.get(4);
		originalStock = ints.get(3);
		buyer = strings.get(5).isEmpty() ? null : UUID.fromString(strings.get(5));
		buyerDisplayName = strings.get(6);
		String bidMap = strings.get(7);

		if(!bidMap.isEmpty()) {
			this.bidMap.clear();

			String[] entrySplit = bidMap.split(",");
			for(String s : entrySplit) {
				String[] dataSplit = s.split(":");
				this.bidMap.put(UUID.fromString(dataSplit[0]), Integer.parseInt(dataSplit[1]));
			}
		}

		String names = strings.get(8);
		if(names.isEmpty()) return;
		String[] nameSplit = names.split(",");
		for(String s : nameSplit) {
			String[] dataSplit = s.split(":");
			bidderDisplayNames.put(UUID.fromString(dataSplit[0]), dataSplit[1]);
		}
	}

	public int getHighestBid() {
		int highest = 0;
		for(Integer value : bidMap.values()) {
			if(value > highest) highest = value;
		}
		return highest;
	}

	public int getHighestPrice() {
		if(getHighestBid() > binPrice) return startingBid;
		else if(binPrice == -1) return startingBid;
		else return binPrice;
	}

	public UUID getHighestBidder() {
		UUID bidder = null;
		int highestValue = 0;
		for(Map.Entry<UUID, Integer> entry : bidMap.entrySet()) {
			if(entry.getValue() > highestValue) {
				bidder = entry.getKey();
				highestValue = entry.getValue();
			}
		}
		return bidder;
	}

	public long getTimeRemaining() {
		return creationTime + listingLength - System.currentTimeMillis();
	}

	public ItemStack getItemStack() {
		AItemStackBuilder builder = new AItemStackBuilder(itemData.clone())
				.setName(itemData.getItemMeta().getDisplayName());
		ALoreBuilder loreBuilder = new ALoreBuilder(itemData.getItemMeta().getLore());
		loreBuilder.addLore("&8&m------------------------");
		loreBuilder.addLore("&7Seller: " + ownerDisplayName, "");
		if(startingBid != -1) {
			if(bidMap.isEmpty()) loreBuilder.addLore("&7Starting Bid: &f" + startingBid + " Souls");
			else {
				loreBuilder.addLore("&7Highest Bid: &f" + getHighestBid() + " Souls");
				loreBuilder.addLore("&7Bidder: " + bidderDisplayNames.get(getHighestBidder()));
			}
			loreBuilder.addLore("");
		}
		if(binPrice != -1) {
			loreBuilder.addLore("&7BIN Price: &f" + binPrice + " Souls" + (stackBIN ? " &8(Per Item)" : ""));
			if(stackBIN) loreBuilder.addLore("&7Stock: &a" + itemData.getAmount() + " Items");

			loreBuilder.addLore("");
		}
		loreBuilder.addLore("&7Time Remaining: &e" + getRemainingTimeString(creationTime, listingLength));
		loreBuilder.addLore("&8&m------------------------");
		loreBuilder.addLore("", "&eClick to Inspect");

		builder.setLore(loreBuilder);
		return builder.getItemStack();
	}

	public static String getRemainingTimeString(long creationDate, long duration) {
		long currentTime = System.currentTimeMillis();
		long endTime = creationDate + duration;
		long remainingTime = endTime - currentTime;
		if (remainingTime <= 0) {
			return "Listing has expired";
		}
		return Formatter.formatDurationFull(remainingTime, true);
	}

	public boolean hasEnded() {
		return hasEnded || getTimeRemaining() <= 0;
	}

	public int getMinimumBid() {
		if(bidMap.isEmpty()) return startingBid;
		else return (int) (getHighestBid() * 1.2);
	}

	public List<Map.Entry<UUID, Integer>> sortBidMap() {
		List<Map.Entry<UUID, Integer>> list = new ArrayList<>(bidMap.entrySet());
		list.sort((o1, o2) -> o2.getValue() - o1.getValue());
		return list;
	}
}


