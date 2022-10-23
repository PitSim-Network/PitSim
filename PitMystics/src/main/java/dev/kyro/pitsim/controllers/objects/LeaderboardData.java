package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.PrestigeValues;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderboardData {

	public static Map<Leaderboard, LeaderboardData> leaderboards = new HashMap<>();

	private final Leaderboard leaderboard;
	private final String leaderboardData;
	private final Map<UUID, PlayerData> leaderboardDataMap;

	public LeaderboardData(Leaderboard leaderboard, String leaderboardData) {
		this.leaderboard = leaderboard;
		this.leaderboardData = leaderboardData;

		this.leaderboardDataMap = new LinkedHashMap<>();
		String[] playerSplit = leaderboardData.split("\\|");
		for(String data : playerSplit) {
			String[] playerData = data.split(",");
			UUID uuid = UUID.fromString(playerData[0]);
			double value = Double.parseDouble(playerData[2]);

			String[] prestigeAndLevel = playerData[1].split(" ");
			leaderboardDataMap.put(uuid, new PlayerData(value, PrestigeValues.getPlayerPrefix(
					Integer.parseInt(prestigeAndLevel[0]), Integer.parseInt(prestigeAndLevel[1]))));
		}

		leaderboards.put(leaderboard, this);
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	public String getLeaderboardData() {
		return leaderboardData;
	}

	public Map<UUID, PlayerData> getLeaderboardDataMap() {
		return leaderboardDataMap;
	}

	public String getPrefix(UUID uuid) {
		if(!leaderboardDataMap.containsKey(uuid)) return null;
		return leaderboardDataMap.get(uuid).prefix;
	}

	public PlayerData getValue(UUID uuid) {
		return leaderboardDataMap.get(uuid);
	}

	public static LeaderboardData getLeaderboardData(Leaderboard leaderboard) {
		return leaderboards.get(leaderboard);
	}

	public static class PlayerData {

		public double primaryValue;
		public String prefix;

		public PlayerData(double primaryValue, String prefix) {
			this.primaryValue = primaryValue;
			this.prefix = prefix;
		}
	}

}
