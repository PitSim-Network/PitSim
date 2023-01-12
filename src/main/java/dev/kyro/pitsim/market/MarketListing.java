package dev.kyro.pitsim.market;

import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.HashMap;
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

	public MarketListing(UUID marketUUID, UUID ownerUUID, String itemData, int startingBid, int binPrice, boolean stackBIN, long listingLength, long creationTime, String bidMap) {
		this.marketUUID = marketUUID;
		this.ownerUUID = ownerUUID;
		this.startingBid = startingBid;
		this.binPrice = binPrice;
		this.stackBIN = stackBIN;
		this.itemData = StorageProfile.deserialize(itemData);
		this.listingLength = listingLength;
		this.creationTime = creationTime;
		this.creationTime = System.currentTimeMillis();
		this.bidMap = new HashMap<>();

		if(bidMap.isEmpty()) return;
		String[] entrySplit = bidMap.split(",");
		for(String s : entrySplit) {
			String[] dataSplit = s.split(":");
			this.bidMap.put(UUID.fromString(dataSplit[0]), Integer.parseInt(dataSplit[1]));
		}
	}
}


