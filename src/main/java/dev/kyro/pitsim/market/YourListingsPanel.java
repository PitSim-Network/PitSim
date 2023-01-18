package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YourListingsPanel extends AGUIPanel {
	public YourListingsPanel(AGUI gui) {
		super(gui);

		for(int i = 0; i < 54; i++) {
			if(i < 9 || i > 45) {
				getInventory().setItem(i, new AItemStackBuilder(Material.STAINED_GLASS_PANE,1, 15).setName(" ").getItemStack());
				continue;
			}

			if(i % 9 != 0 && i % 9 != 4 && i % 9 != 8) continue;
			getInventory().setItem(i, new AItemStackBuilder(Material.STAINED_GLASS_PANE,1, 15).setName(" ").getItemStack());
		}

		List<MarketListing> soulListings = new ArrayList<>();
		
		for(MarketListing listing : MarketManager.listings) {

			if(listing.hasEnded && listing.ownerUUID.equals(player.getUniqueId()) && listing.claimableSouls > 0) {
				soulListings.add(listing);
			}

			List<UUID> bidders = new ArrayList<>(listing.bidMap.keySet());
			if(bidders.contains(player.getUniqueId()) && !listing.buyer.equals(player.getUniqueId())) soulListings.add(listing);
		}

		List<MarketListing> itemListings = new ArrayList<>();
		for(MarketListing listing : MarketManager.listings) {
			if(!listing.hasEnded || listing.itemClaimed) continue;

			if(listing.startingBid != -1) {
				if(listing.ownerUUID.equals(player.getUniqueId()) && listing.buyer == null) itemListings.add(listing);
				else if(listing.buyer != null && listing.buyer.equals(player.getUniqueId())) itemListings.add(listing);
			} else if(listing.binPrice != -1) {
				if(listing.ownerUUID.equals(player.getUniqueId())) itemListings.add(listing);
			}
		}

		List<MarketListing> combined = new ArrayList<>(soulListings);
		boolean firstIterationComplete = false;

		int listIndex = 0;
		for(int i = 10; i < 40; i++) {
			if(i % 9 < 1 || i % 9 > 3) continue;

			if(listIndex == combined.size()) {
				if(!firstIterationComplete) {
					combined = itemListings;
					firstIterationComplete = true;
					listIndex = 0;
				} else break;
			}

			MarketListing listing = combined.get(listIndex);
			if(!firstIterationComplete) {
				AItemStackBuilder soulBuilder = new AItemStackBuilder(Material.INK_SACK, listing.claimableSouls, 7)
						.setName("&fClaimable Souls");

				ALoreBuilder loreBuilder;
				if(listing.ownerUUID.equals(player.getUniqueId())) {
					loreBuilder = new ALoreBuilder(
							"&7Sold: " + listing.itemData.getItemMeta().getDisplayName() + (listing.stackBIN ? " &8x" + (listing.originalStock - listing.itemData.getAmount()) : ""),
							"&7Price: &f" + (listing.stackBIN ? listing.binPrice + " Souls &8(Per Item)" : listing.claimableSouls + " Souls"),
							listing.stackBIN ? "&7Total Price: &f" + (listing.claimableSouls) + " Souls" : "&7Sold to: &f" +
									(listing.buyerDisplayName),
							"",
							"&eClick to claim Souls!"
					);
				} else {
					loreBuilder = new ALoreBuilder(
							"&7Item: " + listing.itemData.getItemMeta().getDisplayName(),
							"&7Winner: " + listing.buyerDisplayName,
							"&7Your Bid: &f" + listing.bidMap.get(player.getUniqueId()) + " Souls",
							"",
							"&eClick to claim Souls!"
					);
				}
				soulBuilder.setLore(loreBuilder.getLore());
				getInventory().setItem(i, soulBuilder.getItemStack());
			}

			if(firstIterationComplete) {
				AItemStackBuilder itemBuilder = new AItemStackBuilder(listing.itemData.clone())
						.setName(listing.itemData.getItemMeta().getDisplayName() + (listing.stackBIN ? " &8x" + (listing.itemData.getAmount()) : ""));

				ALoreBuilder loreBuilder = new ALoreBuilder();
				loreBuilder.addLore(listing.itemData.getItemMeta().getLore());
				loreBuilder.addLore("&8&m------------------------");
				if(listing.ownerUUID.equals(player.getUniqueId())) loreBuilder.addLore("&7" + (listing.stackBIN ? "These items" : "This item") + " did not sell");
				else loreBuilder.addLore("&7You won this item!");
				loreBuilder.addLore("&8&m------------------------", "", "&eClick to claim item!");

				itemBuilder.setLore(loreBuilder.getLore());
				getInventory().setItem(i, itemBuilder.getItemStack());
			}
			listIndex++;
		}
	}

	@Override
	public String getName() {
		return "   Your Claims    Your Listings";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
