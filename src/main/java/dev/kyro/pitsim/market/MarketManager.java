package dev.kyro.pitsim.market;

import dev.kyro.pitsim.PitSim;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketManager {

	public static final int DEFAULT_MAX_LISTINGS = 3;
	public static List<MarketListing> listings = new ArrayList<>();

	public static MarketListing getListing(UUID listingUUID) {
		for(MarketListing listing : listings) {
			if(listing.marketUUID.equals(listingUUID)) return listing;
		}
		return null;
	}

	public static List<MarketListing> getListings(UUID ownerUUID) {
		List<MarketListing> playerListings = new ArrayList<>();
		for(MarketListing listing : listings) {
			if(listing.ownerUUID.equals(ownerUUID)) playerListings.add(listing);
		}
		return playerListings;
	}

}
