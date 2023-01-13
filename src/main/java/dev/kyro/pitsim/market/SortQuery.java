package dev.kyro.pitsim.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortQuery {

	private final PrimarySortType primarySortType;
	private final SecondarySortType secondarySortType;
	private final String sortParameter;
	private final MarketListing[] listings;

	public SortQuery(PrimarySortType primarySortType, SecondarySortType secondarySortType, String sortParameter) {
		 listings = new MarketListing[MarketManager.listings.size()];

		 this.primarySortType = primarySortType;
		 this.secondarySortType = secondarySortType;
		 this.sortParameter = sortParameter;

		 List<MarketListing> sortedListings = getSortedList(primarySortType, secondarySortType, sortParameter);
		 for(int i = 0; i < listings.length; i++) {
			 listings[i] = sortedListings.get(i);
		 }
	}

	public SecondarySortType getSecondarySortTypefromListing(MarketListing listing) {
		if(listing.stackBIN) return SecondarySortType.BIN;
		if(listing.startingBid != -1 && listing.binPrice != -1) return SecondarySortType.ALL;
		if(listing.startingBid != -1) return SecondarySortType.AUCTION;
		return SecondarySortType.BIN;
	}

	public List<MarketListing> getSortedList(PrimarySortType primarySortType, SecondarySortType secondarySortType, String sortParameter) {
		List<MarketListing> sortedList = new ArrayList<>();

		if(primarySortType == PrimarySortType.SEARCH) {
			sortedList.addAll(MarketManager.listings);
			sortedList.sort(Comparator.comparing(m -> m.itemData.getItemMeta().getDisplayName()));
			sortedList.sort((m1, m2) -> m1.itemData.getItemMeta().getDisplayName().compareToIgnoreCase(sortParameter));
			return sortedList;
		}

		listings:
		for(MarketListing listing : MarketManager.listings) {
			SecondarySortType listingType = getSecondarySortTypefromListing(listing);
			if(listingType != secondarySortType) continue;

			switch(primarySortType) {
				case PRICE_LOW:
					for(int i = 0; i < sortedList.size(); i++) {
						if(listing.getHighestPrice() < sortedList.get(i).getHighestPrice()) {
							sortedList.add(i, listing);
							continue listings;
						}
					}
					sortedList.add(listing);
					break;
				case PRICE_HIGH:
					for(int i = 0; i < sortedList.size(); i++) {
						if(listing.getHighestPrice() > sortedList.get(i).getHighestPrice()) {
							sortedList.add(i, listing);
							continue listings;
						}
					}
					sortedList.add(listing);
					break;
				case DATE_NEW:
					for(int i = 0; i < sortedList.size(); i++) {
						if(listing.creationTime > sortedList.get(i).creationTime) {
							sortedList.add(i, listing);
							continue listings;
						}
					}
					sortedList.add(listing);
					break;
				case DATE_OLD:
					for(int i = 0; i < sortedList.size(); i++) {
						if(listing.creationTime < sortedList.get(i).creationTime) {
							sortedList.add(i, listing);
							continue listings;
						}
					}
					sortedList.add(listing);
					break;
			}
		}
		return sortedList;
	}

	enum PrimarySortType {
		PRICE_LOW,
		PRICE_HIGH,
		DATE_NEW,
		DATE_OLD,
		SEARCH;
	}

	enum SecondarySortType {
		AUCTION,
		BIN,
		ALL;
	}

	public PrimarySortType getPrimarySortType() {
		return primarySortType;
	}

	public SecondarySortType getSecondarySortType() {
		return secondarySortType;
	}

	public MarketListing[] getListings() {
		return listings;
	}

	public String getSortParameter() {
		return sortParameter;
	}

}
