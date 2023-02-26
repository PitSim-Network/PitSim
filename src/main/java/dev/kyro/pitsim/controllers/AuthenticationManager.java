package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class AuthenticationManager implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		if(strings.isEmpty()) return;

		if(strings.get(0).equals("AUTH_SUCCESS")) {
			UUID playerUUID = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(playerUUID);
			if(player == null) return;

			AOutput.send(player, "&9&lLINK!&7 Great job verifying!");
		}
	}

	public static void sendAuthenticationLink(Player player, UUID clientState) {
		TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&9&lLINK!&7 Click me to link your discord account"));
		text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/api/oauth2/authorize?" +
				"client_id=841567626466951171&redirect_uri=http%3A%2F%2F51.81.48.25%3A3000&response_type=code&" +
				"scope=identify%20guilds.join&state=" + clientState.toString()));
		player.spigot().sendMessage(text);
	}

	public enum AuthStatus {
		DISCORD_ALREADY_AUTHENTICATED,
		MINECRAFT_ALREADY_AUTHENTICATED,
		READY_FOR_AUTHENTICATION
	}
}
