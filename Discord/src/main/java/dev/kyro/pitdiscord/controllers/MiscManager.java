package dev.kyro.pitdiscord.controllers;

import dev.kyro.pitdiscord.Constants;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiscManager implements Listener {

	public static List<String> wijiAccounts = new ArrayList<>();
	static {
		wijiAccounts.add("5c06626c-66bf-48a9-84cb-f2683e6aca87");
		wijiAccounts.add("e6654529-0d8c-4f86-954b-66e53e65ab33");
		wijiAccounts.add("346732b3-c8a3-4861-a3bb-1a288f195888");
	}

//	@EventHandler
	public void onWijiLeave(PlayerQuitEvent event) {
		if(!wijiAccounts.contains(event.getPlayer().getUniqueId().toString())) return;
		try {
			Objects.requireNonNull(DiscordManager.JDA.getTextChannelById(Constants.WIJI_REMEMBER_TO_PUSH_CHANNEL))
					.sendMessage("<@741455066795343932> remember to push").queue();
		} catch(Exception ignored) {
			System.out.println("wiji channel does not exist");
		}
	}
}
