package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackManager implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.promptPack) {
			event.getPlayer().setResourcePack("https://cdn.discordapp.com/attachments/803483152630677524/1035038648552394782/Nebula_PitEdit.zip");

//			event.getPlayer().setResourcePack("https://cdn.discordapp.com/attachments/803483152630677524/903075400442314772/PitSim.zip");
		} else {
			TextComponent nonClick = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&lWe recommend you use our resource pack for a better\n&c&lgameplay experience. To do so, click "));
			TextComponent click = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lhere."));
			click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resource"));
			nonClick.addExtra(click);
			event.getPlayer().sendMessage(nonClick);
		}
	}

	@EventHandler
	public void onPrompt(PlayerResourcePackStatusEvent event) {
		if(event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
			pitPlayer.promptPack = true;
		}
		if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
			pitPlayer.promptPack = false;
		}
	}
}
