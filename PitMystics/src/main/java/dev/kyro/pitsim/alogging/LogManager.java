package dev.kyro.pitsim.alogging;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LogManager implements Listener {
	public static void onJewelComplete(Player player, PitEnchant enchant, int maxLives) {
		sendLogMessage(LogType.COMPLETE_JEWEL, player.getName() + " created " + enchant.getDisplayName() + " III " + maxLives + "/" + maxLives);
	}

	public static void onBoosterActivate(Player player, Booster booster) {
		sendLogMessage(LogType.BOOSTER_USE, player.getName() + " activated " + booster.name);
	}

	public static void onItemGem(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.GEM_ITEM, player.getName() + " gemmed " + serializeItem(itemStack));
	}

	public static void onItemBreak(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.ITEM_BROKEN, player.getName() + " broke " + serializeItem(itemStack));
	}

	public static void onItemRepair(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.REPAIR_ITEM, player.getName() + " repaired " + serializeItem(itemStack));
	}

	public static void onItemLifeLost(Player player, ItemStack itemStack) {
		sendLogMessage(LogType.LIFE_LOST, player.getName() + " lost a life on " + serializeItem(itemStack));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		sendLogMessage(LogType.PLAYER_CHAT, player.getName() + " >> " + ChatColor.stripColor(event.getMessage()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;
		sendLogMessage(LogType.PLAYER_KILL, killEvent.getKillerPlayer().getName() + " killed " + killEvent.getDeadPlayer().getName());
	}

	public static void sendLogMessage(LogType logType, String message) {
		Date date = Misc.convertToEST(new Date());
		assert date != null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		PluginMessage pluginMessage = new PluginMessage();
		pluginMessage.writeString("LOG").writeString(logType.toString()).writeString(PitSim.serverName);
		pluginMessage.writeString("[" + dateFormat.format(date) + "][" + PitSim.serverName + "][" + logType + "]: " + message);
		pluginMessage.writeString(message);
		pluginMessage.writeLong(date.getTime());
		pluginMessage.send();
	}

	public static String serializeItem(ItemStack itemStack) {
		String serializedItem = "";
		if(Misc.isAirOrNull(itemStack) || !itemStack.hasItemMeta()) return addBraces(serializedItem);
		serializedItem += itemStack.getAmount() + "x";

		NBTItem nbtItem = new NBTItem(itemStack);
		ItemMeta itemMeta = itemStack.getItemMeta();
		serializedItem += " " + itemStack.getType();

		if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
			serializedItem += " " + nbtItem.getString(NBTTag.ITEM_UUID.getRef()) + " " +
					nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) + "/" + nbtItem.getInteger(NBTTag.MAX_LIVES.getRef());
			if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) serializedItem += " Gemmed";
			if(EnchantManager.isJewelComplete(itemStack)) serializedItem += " Jewel: " +
					EnchantManager.getEnchant(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()));
			serializedItem += " Enchants:";
			for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnItem(itemStack).entrySet()) {
				serializedItem += " " + entry.getKey().getDisplayName() + " " + entry.getValue();
			}
		}
		if(!itemMeta.hasDisplayName()) return addBraces(serializedItem);
		serializedItem += " " + ChatColor.stripColor(itemMeta.getDisplayName());
		return addBraces(serializedItem);
	}

	public static String addBraces(String string) {
		return "{" + string + "}";
	}
}
