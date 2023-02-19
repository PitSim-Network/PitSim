package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class ChatTriggerManager implements Listener {
	public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&9&lDATA!&7 ");
	private static final List<UUID> subscribedPlayers = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : getSubscribedPlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					sendProgressionInfo(pitPlayer);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public static void sendPerksInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new LinkedHashMap<>();

		List<String> perks = new ArrayList<>();
		pitPlayer.pitPerks.forEach(pitPerk -> perks.add(pitPerk.displayName));
		dataMap.put("perks", encodeList(perks));

		List<String> killstreaks = new ArrayList<>();
		pitPlayer.killstreaks.forEach(killstreak -> killstreaks.add(killstreak.displayName));
		dataMap.put("killstreaks", encodeList(killstreaks));

		dataMap.put("megastreak", pitPlayer.megastreak.getName());

		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendProgressionInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new LinkedHashMap<>();
		dataMap.put("xp", PrestigeValues.getTotalXPForPrestige(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP));
		dataMap.put("totalXPForPres", PrestigeValues.getTotalXPForPrestige(pitPlayer.prestige));
		dataMap.put("currentGReq", PrestigeValues.getTotalXPForPrestige(pitPlayer.prestige));
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendPrestigeInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new LinkedHashMap<>();
		dataMap.put("totalGReqForPres", PrestigeValues.getPrestigeInfo(pitPlayer.prestige).goldReq);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendAuctionInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new LinkedHashMap<>();
		dataMap.put("auctionData", CrossServerMessageManager.auctionNames);
		dataMap.put("auctionEnd", CrossServerMessageManager.auctionEndTime);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static Object encodeObject(Object object) {
		if(object instanceof Map) {
			return encodeMap((Map<String, Object>) object);
		} else if(object instanceof Object[]) {
			return encodeArray((Object[]) object);
		} else if(object instanceof List) {
			return encodeList((List<Object>) object);
		}
		return String.valueOf(object).replaceAll("\u00A7", "&");
	}

	public static JSONArray encodeArray(Object... array) {
		return encodeList(Arrays.asList(array));
	}

	public static JSONArray encodeList(List<?> list) {
		JSONArray jsonArray = new JSONArray();
		for(Object object : list) jsonArray.add(encodeObject(object));
		return jsonArray;
	}

	public static JSONObject encodeMap(Map<String, Object> dataMap) {
		JSONObject jsonObject = new JSONObject();
		for(Map.Entry<String, Object> entry : dataMap.entrySet()) jsonObject.put(entry.getKey(), encodeObject(entry.getValue()));
		return jsonObject;
	}

	public static void subScribePlayer(Player player) {
		if(subscribedPlayers.contains(player.getUniqueId())) return;
		subscribedPlayers.add(player.getUniqueId());

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		sendPrestigeInfo(pitPlayer);
		sendAuctionInfo(pitPlayer);
		sendPerksInfo(pitPlayer);
	}

	public static boolean isSubscribed(Player player) {
		return subscribedPlayers.contains(player.getUniqueId());
	}

	public static List<Player> getSubscribedPlayers() {
		List<Player> subscribedPlayers = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) if(isSubscribed(player)) subscribedPlayers.add(player);
		return subscribedPlayers;
	}

	public static void sendData(Player player, JSONObject data) {
		player.sendMessage(PREFIX + data.toString());
	}
}
