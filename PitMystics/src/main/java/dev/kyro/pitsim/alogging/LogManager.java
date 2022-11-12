package dev.kyro.pitsim.alogging;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager implements Listener {

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		sendLogMessage(LogType.TEST, player.getName() + " either started or stopped sneaking");
	}

	public static void sendLogMessage(LogType logType, String message) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println("[" + simpleDateFormat.format(Misc.convertToEST(new Date())) + "]" + logType.toString());
		PluginMessage pluginMessage = new PluginMessage();
		pluginMessage.writeString("LOG").writeString(PitSim.serverName);
		pluginMessage.writeString("[" + simpleDateFormat.format(Misc.convertToEST(new Date())) + "][" + PitSim.serverName + "]:" + logType);
		pluginMessage.writeString(message);
		pluginMessage.send();
	}
}
