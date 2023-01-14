package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	public MarketListing(PluginMessage message) {
		updateListing(message);
		bidMap = new HashMap<>();
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

		String bidMap = strings.get(4);

		if(bidMap.isEmpty()) return;
		String[] entrySplit = bidMap.split(",");
		for(String s : entrySplit) {
			String[] dataSplit = s.split(":");
			this.bidMap.put(UUID.fromString(dataSplit[0]), Integer.parseInt(dataSplit[1]));
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
		AItemStackBuilder builder = new AItemStackBuilder(itemData.getType(), itemData.getAmount(), itemData.getDurability())
				.setLore(new ALoreBuilder(
						"&7Owner: &f" + ownerUUID.toString(),
						"&7Market UUID: &f" + marketUUID.toString(),
						"&7Starting Bid: &f" + startingBid,
						"&7Bin Price: &f" + binPrice,
						"&7Stack BIN: &f" + stackBIN,
						"&7Highest Bid: &f" + getHighestBid(),
						"&7Highest Bidder: &f" + getHighestBidder(),
						"&7Highest Price: &f" + getHighestPrice(),
						"&eTime Left: &f" + getRemainingTimeString(creationTime, listingLength)
				));
		return builder.getItemStack();
	}

	public static String getRemainingTimeString(long creationDate, long duration) {
		long currentTime = System.currentTimeMillis();
		long endTime = creationDate + duration;
		long remainingTime = endTime - currentTime;
		if (remainingTime <= 0) {
			return "Listing has expired";
		}
		long days = remainingTime / (24 * 60 * 60 * 1000);
		remainingTime = remainingTime % (24 * 60 * 60 * 1000);
		long hours = remainingTime / (60 * 60 * 1000);
		remainingTime = remainingTime % (60 * 60 * 1000);
		long minutes = remainingTime / (60 * 1000);
		remainingTime = remainingTime % (60 * 1000);
		long seconds = remainingTime / 1000;
		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}

	public boolean hasEnded() {
		return hasEnded || getTimeRemaining() <= 0;
	}


}


