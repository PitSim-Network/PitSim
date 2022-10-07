package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ProxyMessaging implements Listener {


	public static void sendStartup() {
		System.out.println(PitSim.serverName);
		new PluginMessage().writeString("INITIATE STARTUP").writeString(PitSim.serverName).send();
	}

	public static void sendShutdown() {
		new PluginMessage().writeString("INITIATE FINAL SHUTDOWN").writeString(PitSim.serverName).send();
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		System.out.println("Message received");
		PluginMessage message = event.getMessage();
		List<String> strings = event.getMessage().getStrings();
		List<Boolean> booleans = event.getMessage().getBooleans();

		if(strings.size() >= 1 && booleans.size() >= 1 && strings.get(0).equals("SHUTDOWN")) {
			ShutdownManager.initiateShutdown(5);
		}


	}

}
