package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.LeaderboardManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardPlayerData {

	public static List<LeaderboardPlayerData> leaderboardPlayerData = new ArrayList<>();

	private final UUID uuid;
	private final List<Double> leaderboardData;
	private Player player;

	public LeaderboardPlayerData(UUID uuid, List<String> leaderboardData) {

		this.uuid = uuid;
		this.leaderboardData = new ArrayList<>();
		for(String leaderboardDatum : leaderboardData) {
			this.leaderboardData.add(Double.parseDouble(leaderboardDatum));
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		if(offlinePlayer.isOnline()) player = offlinePlayer.getPlayer();

		leaderboardPlayerData.add(this);
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<Double> getLeaderboardData() {
		return leaderboardData;
	}

	public Player getPlayer() {
		return player;
	}

	public double getData(Leaderboard leaderboard) {
		int leaderboardIndex = LeaderboardManager.leaderboards.indexOf(leaderboard);
		return leaderboardData.get(leaderboardIndex);
	}

	public static LeaderboardPlayerData getData(UUID uuid) {
		for(LeaderboardPlayerData leaderboardPlayerData : leaderboardPlayerData) {
			if(leaderboardPlayerData.uuid.equals(uuid)) return leaderboardPlayerData;
		}
		return null;
	}



}
