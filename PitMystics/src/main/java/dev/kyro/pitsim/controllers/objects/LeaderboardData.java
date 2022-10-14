package dev.kyro.pitsim.controllers.objects;

import javafx.util.Pair;

import java.util.*;

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
			if(playerData.length == 2) {
				UUID uuid = UUID.fromString(playerData[0]);
				double value = Double.parseDouble(playerData[1]);
				leaderboardDataMap.put(uuid, new PlayerData(value));
			}
			leaderboardDataMap.put(UUID.fromString(playerData[0]), new PlayerData(Integer.parseInt(playerData[1]),
					Integer.parseInt(playerData[1]), Long.parseLong(playerData[1])));
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

	public PlayerData getValue(UUID uuid) {
		return leaderboardDataMap.get(uuid);
	}

	public static LeaderboardData getLeaderboardData(Leaderboard leaderboard) {
		return leaderboards.get(leaderboard);
	}

	public static class PlayerData {

		public double primaryValue;
		public int prestige;
		public int level;
		public long xp;

		public PlayerData(int prestige, int level, long xp) {
			this.prestige = prestige;
			this.level = level;
			this.xp = xp;
		}

		public PlayerData(double primaryValue) {
			this.primaryValue = primaryValue;
		}
	}

}
