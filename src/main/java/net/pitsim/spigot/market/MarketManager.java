package net.pitsim.spigot.market;

import net.pitsim.spigot.events.PitQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class MarketManager implements Listener {

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

	public static List<MarketListing> getActiveListings(UUID ownerUUID) {
		List<MarketListing> playerListings = new ArrayList<>();
		for(MarketListing listing : listings) {
			if(listing.ownerUUID.equals(ownerUUID) && !listing.hasEnded()) playerListings.add(listing);
		}
		return playerListings;
	}

	@EventHandler
	public void onLeave(PitQuitEvent event) {
		UUID playerUUID = event.getPlayer().getUniqueId();
		if(MarketAsyncTask.taskMap.containsKey(playerUUID)) {
			MarketAsyncTask task = MarketAsyncTask.taskMap.get(playerUUID);
			task.getTimeout().cancel();
			task.getFailure().run();
			MarketAsyncTask.taskMap.remove(playerUUID);
		}
		MarketAsyncTask.taskMap.remove(playerUUID);
	}

	public enum ListingLimit {
		MEMBER("&7Member", 3, "group.default"),
		LEGENDARY("&eLegendary", 4, "group.legendary"),
		OVERPOWERED("&5Overpowered", 5, "group.overpowered"),
		EXTRAORDINARY("&3Extraordinary", 6, "group.extraordinary"),
		MIRACULOUS("&bMiraculous", 8, "group.miraculous"),
		UNTHINKABLE("&6Unthinkable", 10, "group.unthinkable"),
		ETERNAL("&4Eternal", 12, "group.eternal"),
		DEVELOPER("&9Developer", 12, "group.developer");

		public final String rankName;
		public final int limit;
		public final String permission;

		ListingLimit(String rankName, int limit, String permission) {
			this.rankName = rankName;
			this.limit = limit;
			this.permission = permission;
		}

		public static ListingLimit getRank(Player player) {
			List<ListingLimit> ranks = new ArrayList<>(Arrays.asList(values()));
			Collections.reverse(ranks);
			for(ListingLimit value : ranks) if(player.hasPermission(value.permission)) return value;
			return MEMBER;
		}

		public static ListingLimit getMinimumRequiredRank(int pages) {
			for(ListingLimit value : values()) if(value.limit >= pages) return value;
			return MEMBER;
		}
	}

}
