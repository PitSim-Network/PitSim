package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.Mappable;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.overworld.ReallyToxic;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class ChatTriggerManager implements Listener {
	public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&9&lDATA!&7 ");
	private static final List<Player> subscribedPlayers = new ArrayList<>();
	public static final Map<Player, Long> lastSendLevelData = new HashMap<>();

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent) {
		if(isSubscribed(attackEvent.getAttackerPlayer())) {
			Map<String, Object> dataMap = new HashMap<>();

			Map<String, Object> playerInfoMap = new HashMap<>();
			PitEntityType entityType = Misc.getEntity(attackEvent.getDefender());
			playerInfoMap.put("targetType", entityType);
			if(entityType == PitEntityType.REAL_PLAYER) {
				playerInfoMap.put("playerUsername", attackEvent.getDefenderPlayer().getName());
				playerInfoMap.put("playerDisplayName", Misc.getDisplayName(attackEvent.getDefenderPlayer()));
			}

			dataMap.put("lastAttack", encodeMap(playerInfoMap));
			sendData(attackEvent.getAttackerPlayer(), encodeMap(dataMap));
		}


		if(isSubscribed(attackEvent.getDefenderPlayer())) {
			Map<String, Object> dataMap = new HashMap<>();

			Map<String, Object> playerInfoMap = new HashMap<>();
			PitEntityType entityType = Misc.getEntity(attackEvent.getAttacker());
			playerInfoMap.put("targetType", entityType);
			if(entityType == PitEntityType.REAL_PLAYER) {
				playerInfoMap.put("playerUsername", attackEvent.getAttackerPlayer().getName());
				playerInfoMap.put("playerDisplayName", Misc.getDisplayName(attackEvent.getAttackerPlayer()));
			}

			dataMap.put("lastDefence", encodeMap(playerInfoMap));
			sendData(attackEvent.getDefenderPlayer(), encodeMap(dataMap));
		}
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		Player player = event.getPlayer();
		subscribedPlayers.remove(player);
	}

	public static void sendConstants(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("maxToxic", ReallyToxic.getMaxReduction());
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendPerksInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();

		List<String> perks = new ArrayList<>();
		pitPlayer.pitPerks.forEach(pitPerk -> perks.add(pitPerk.displayName));
		dataMap.put("perks", encodeList(perks));

		List<String> killstreaks = new ArrayList<>();
		pitPlayer.killstreaks.forEach(killstreak -> killstreaks.add(killstreak.displayName));
		dataMap.put("killstreaks", encodeList(killstreaks));

		dataMap.put("megastreak", pitPlayer.getMegastreak().getRefName());

		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendProgressionInfo(PitPlayer pitPlayer) {
		long currentTick = lastSendLevelData.getOrDefault(pitPlayer.player, 0L);
		if(currentTick == PitSim.currentTick) return;
		lastSendLevelData.put(pitPlayer.player, PitSim.currentTick);

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("xp", PrestigeValues.getTotalXPForPrestige(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP));
		dataMap.put("totalXPForPres", PrestigeValues.getTotalXPForPrestige(pitPlayer.prestige));
		dataMap.put("currentGReq", pitPlayer.goldGrinded);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendPrestigeInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("totalGReqForPres", PrestigeValues.getPrestigeInfo(pitPlayer.prestige).getGoldReq());
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendAuctionInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("auctionData", CrossServerMessageManager.auctionItems);
		dataMap.put("auctionEnd", CrossServerMessageManager.auctionEndTime);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendUberInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("maxUbers", Uberstreak.getMaxUbers(pitPlayer.player));
		dataMap.put("ubersLeft", pitPlayer.dailyUbersLeft);
		dataMap.put("uberResetTime", pitPlayer.uberReset);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendBountyInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("bounty", pitPlayer.bounty);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendToxicInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("toxic", HitCounter.getCharge(pitPlayer.player, ReallyToxic.INSTANCE));
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static void sendBoosterInfo(PitPlayer pitPlayer) {
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> boosterDataMap = new HashMap<>();
		for(Booster booster : BoosterManager.boosterList) boosterDataMap.put(booster.name, booster.minutes);
		dataMap.put("boosterTime", boosterDataMap);
		sendData(pitPlayer.player, encodeMap(dataMap));
	}

	public static Object encodeObject(Object object) {
		if(object instanceof Mappable) {
			return encodeMap(((Mappable) object).getAsMap());
		} else if(object instanceof Map) {
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

	public static void subscribePlayer(Player player) {
		if(subscribedPlayers.contains(player)) return;
		subscribedPlayers.add(player);
		sendAllData(player);
	}

	public static void sendAllData(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		sendConstants(pitPlayer);
		sendPerksInfo(pitPlayer);
		sendProgressionInfo(pitPlayer);
		sendPrestigeInfo(pitPlayer);
		sendAuctionInfo(pitPlayer);
		sendUberInfo(pitPlayer);
		sendBountyInfo(pitPlayer);
		sendToxicInfo(pitPlayer);
		sendBoosterInfo(pitPlayer);
	}

	public static boolean isSubscribed(Player player) {
		if(player == null) return false;
		return subscribedPlayers.contains(player);
	}

	public static List<Player> getSubscribedPlayers() {
		List<Player> subscribedPlayers = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) if(isSubscribed(player)) subscribedPlayers.add(player);
		return subscribedPlayers;
	}

	public static void sendData(Player player, JSONObject data) {
		if(!isSubscribed(player)) return;
		player.sendMessage(PREFIX + data.toString());
	}
}
