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
		List<Boolean> booleans = message.getBooleans();

		if(strings.size() >= 3 && strings.get(0).equals("MARKET UPDATE")) {
			UUID listingUUID = UUID.fromString(strings.get(1));

			MarketListing listing = MarketManager.getListing(listingUUID);
			if(listing != null) {
				listing.updateListing(message);

				for(YourListingsPanel panel : YourListingsPanel.panels) {
					panel.placeClaimables();
				}
				return;
			}

			listing = new MarketListing(message);
			MarketManager.listings.add(listing);

		}

		if(strings.size() >= 2 && strings.get(0).equals("MARKET REMOVAL")) {
			UUID listingUUID = UUID.fromString(strings.get(1));

			MarketListing listing = MarketManager.getListing(listingUUID);
			if(listing != null) {
				MarketManager.listings.remove(listing);

				for(YourListingsPanel panel : YourListingsPanel.panels) {
					panel.placeClaimables();
				}
			}
		}

		if(strings.size() >= 3 && strings.get(0).equals("MARKET ASYNC")) {
			UUID playerUUID = UUID.fromString(strings.get(1));

			MarketAsyncTask task = MarketAsyncTask.getTask(playerUUID);
			task.respond(booleans.get(0));
		}
	}
}
