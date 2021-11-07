package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackManager implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());

		FileConfiguration playerData = APlayerData.getPlayerData(event.getPlayer());
		if(playerData.contains("promptPack")) {
			if(playerData.getBoolean("promptPack")) {
				event.getPlayer().setResourcePack("https://cdn.discordapp.com/attachments/803483152630677524/903075400442314772/PitSim.zip");
			}
		} else {
			TextComponent nonClick = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&c&lWe recommend you use our resource pack for a better\n&c&lgameplay experience. To do so, click "));
			TextComponent click = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lhere."));
			click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resource"));

			nonClick.addExtra(click);

			event.getPlayer().sendMessage(nonClick);

		}
	}

	@EventHandler
	public void onPrompt(PlayerResourcePackStatusEvent event) {
		if(event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED) {
			FileConfiguration playerData = APlayerData.getPlayerData(event.getPlayer());
			playerData.set("promptPack", true);
			APlayerData.savePlayerData(event.getPlayer());
		}
		if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
			FileConfiguration playerData = APlayerData.getPlayerData(event.getPlayer());
			playerData.set("promptPack", false);
			APlayerData.savePlayerData(event.getPlayer());
		}
	}
}
