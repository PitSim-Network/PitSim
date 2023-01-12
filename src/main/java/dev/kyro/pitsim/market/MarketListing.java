package dev.kyro.pitsim.market;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarketListing implements Serializable {
	private static final long serialVersionUID = -6903933977591709194L;

	private UUID marketUUID;
	private UUID ownerUUID;
	private long creationTime;

	private long listingLength;

	private Map<UUID, Integer> bidMap;
	private String itemData;

	//Auction Data; -1 = Auction disabled
	private int startingBid = -1;
	//Bin Data; -1 = Bin disabled
	private int binPrice = -1;
	//Weather or not multiple items are being sold; Auction mode disabled by default if true
	private boolean stackBIN;

	private int claimableSouls = 0;
	private boolean itemClaimed = false;

	private boolean hasEnded = false;

	public MarketListing(UUID ownerUUID, String itemData, int startingBid, int binPrice, boolean stackBIN, long listingLength) {
		this.ownerUUID = ownerUUID;
		this.startingBid = startingBid;
		this.binPrice = binPrice;
		this.stackBIN = stackBIN;
		this.itemData = itemData;
		this.listingLength = listingLength;
		this.marketUUID = UUID.randomUUID();
		this.creationTime = System.currentTimeMillis();
		this.bidMap = new HashMap<>();
	}

	public MarketListing() {

	}

	public void placeBid(UUID playerUUID, int bidAmount) {

	}

	public void bin(UUID playerUUID, int amount) {

	}

	public void claimItem(UUID playerUUID) {

	}

	public void claimSouls(UUID playerUUID) {

	}

	public void remove() {

	}

	public void update() {

	}

	public void save() {

	}

	public int getMinimumBid() {
		if(startingBid == -1) return -1;
		return Math.max(getHighestBid(), startingBid);
	}

	public int getHighestBid() {
		int highest = 0;
		for(Integer value : bidMap.values()) {
			if(value > highest) highest = value;
		}
		return highest;
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

	public boolean isExpired() {
		return creationTime + listingLength <= System.currentTimeMillis();
	}

	public boolean isEnded() {
		return hasEnded;
	}

	public UUID getUUID() {
		return marketUUID;
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public boolean isStackBIN() {
		return stackBIN;
	}

	public Map<UUID, Integer> getBidMap() {
		return bidMap;
	}

	public int getStartingBid() {
		return startingBid;
	}

	public int getBinPrice() {
		return binPrice;
	}

	public int getClaimableSouls() {
		return claimableSouls;
	}

	public boolean isItemClaimed() {
		return itemClaimed;
	}
}


