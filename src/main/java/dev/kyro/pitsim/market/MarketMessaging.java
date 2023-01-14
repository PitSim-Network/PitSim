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

		if(strings.size() >= 3 && strings.get(0).equals("MARKET UPDATE")) {
			UUID listingUUID = UUID.fromString(strings.get(1));

			MarketListing listing = MarketManager.getListing(listingUUID);
			if(listing != null) {
				listing.updateListing(message);
			}

			listing = new MarketListing(message);
			MarketManager.listings.add(listing);
		}
	}
}
