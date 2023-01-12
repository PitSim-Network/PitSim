package dev.kyro.pitsim.market;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class MarketMessaging implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();
		List<Long> longs = message.getLongs();

		if(strings.size() >= 3 && strings.get(0).equals("MARKET UPDATE")) {
			UUID listingUUID = UUID.fromString(strings.get(1));
			UUID ownerUUID = UUID.fromString(strings.get(2));
			int startingBid = ints.get(0);
			int binPrice = ints.get(1);
			boolean stackBIN = booleans.get(0);
			String itemData = strings.get(3);
			long listingLength = longs.get(0);
			long creationTime = longs.get(1);
			String bidMap = strings.get(4);

			MarketListing listing = MarketManager.getListing(listingUUID);
			if(listing != null) {
				listing.marketUUID = listingUUID;
				listing.ownerUUID = ownerUUID;
				listing.startingBid = startingBid;
				listing.binPrice = binPrice;
				listing.stackBIN = stackBIN;
				listing.itemData = StorageProfile.deserialize(itemData);
				listing.listingLength = listingLength;
				listing.creationTime = creationTime;

				String[] entrySplit = bidMap.split(",");
				for(String s : entrySplit) {
					String[] dataSplit = s.split(":");
					listing.bidMap.put(UUID.fromString(dataSplit[0]), Integer.parseInt(dataSplit[1]));
				}
				return;
			}

			listing = new MarketListing(listingUUID, ownerUUID, itemData, startingBid, binPrice, stackBIN, listingLength, creationTime, bidMap);
			MarketManager.listings.add(listing);

		}
	}
}
