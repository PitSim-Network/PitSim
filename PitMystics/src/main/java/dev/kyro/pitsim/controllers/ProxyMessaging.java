package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.controllers.objects.ServerData;
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

	public static void sendServerData() {
		PluginMessage message = new PluginMessage();
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		System.out.println("Message received");
		PluginMessage message = event.getMessage();
		List<String> strings = event.getMessage().getStrings();
		List<Integer> integers = event.getMessage().getIntegers();
		List<Boolean> booleans = event.getMessage().getBooleans();

		System.out.println(strings);

		if(strings.size() >= 1 && strings.get(0).equals("SERVER DATA")) {
			System.out.println("Server data received");
			strings.remove(0);

			for(int i = 0; i < integers.size(); i++) {
				System.out.println(i);

				new ServerData(integers.get(i), strings, integers, booleans);
			}
		}

		if(strings.size() >= 1 && booleans.size() >= 1 && strings.get(0).equals("SHUTDOWN")) {
			ShutdownManager.initiateShutdown(5);
		}


	}

}
