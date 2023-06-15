package net.pitsim.spigot.logging;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager implements Listener {
	public static void onJewelComplete(Player player, PitEnchant enchant, int maxLives) {
		sendLogMessage(LogType.COMPLETE_JEWEL, player.getName() + " created " + enchant.getDisplayName() + " III " + maxLives + "/" + maxLives);
	}

	public static void onBoosterActivate(Player player, Booster booster) {
		sendLogMessage(LogType.BOOSTER_USE, player.getName() + " activated " + booster.name);
	}

	public static void onItemGem(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.GEM_ITEM, player.getName() + " gemmed " + Misc.stringifyItem(itemStack));
	}

	public static void onItemBreak(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.ITEM_BROKEN, player.getName() + " broke " + Misc.stringifyItem(itemStack));
	}

	public static void onItemRepair(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.REPAIR_ITEM, player.getName() + " repaired " + Misc.stringifyItem(itemStack));
	}

	public static void onItemLifeLost(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.LIFE_LOST, player.getName() + " lost a life on " + Misc.stringifyItem(itemStack));
	}

	public static void onIllegalItemRemoved(OfflinePlayer player, ItemStack itemStack) {
		sendLogMessage(LogType.ILLEGAL_ITEM_REMOVED, player.getName() + " had an illegal item removed: " + Misc.stringifyItem(itemStack));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		sendLogMessage(LogType.PLAYER_CHAT, player.getName() + " >> " + ChatColor.stripColor(event.getMessage()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = player.getName() + " executed ";
		if(event.isCancelled()) message += "(cancelled) ";
		sendLogMessage(LogType.PLAYER_COMMAND, message + event.getMessage().toLowerCase());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !PlayerManager.isRealPlayer(killEvent.getDeadPlayer()))
			return;
		sendLogMessage(LogType.PLAYER_KILL, killEvent.getKillerPlayer().getName() + " killed " + killEvent.getDeadPlayer().getName());
	}

	public static void sendLogMessage(LogType logType, String message) {
		OffsetDateTime date = OffsetDateTime.now(PitSim.TIME_ZONE);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

		PluginMessage pluginMessage = new PluginMessage();
		pluginMessage.writeString("LOG").writeString(logType.toString()).writeString(PitSim.serverName);
		pluginMessage.writeString("[" + dateFormat.format(date) + "][" + PitSim.serverName + "][" + logType + "]: " + message);
		pluginMessage.writeString(message);
		pluginMessage.writeLong(date.toInstant().toEpochMilli());
		pluginMessage.send();
	}
}
