package dev.kyro.pitsim.market;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Base64;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.List;

public class MarketMessaging implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		List<Boolean> booleans = message.getBooleans();
		List<Long> longs = message.getLongs();

		if(strings.size() >= 3 && strings.get(0).equals("MARKET UPDATE")) {

			try {
				MarketListing listing = Base64.deserialize(strings.get(2));

				System.out.println(listing);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
