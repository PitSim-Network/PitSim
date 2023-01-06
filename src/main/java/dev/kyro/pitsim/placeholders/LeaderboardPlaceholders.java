package dev.kyro.pitsim.placeholders;

import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.leaderboards.XPLeaderboard;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardPlaceholders extends PlaceholderExpansion {

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if(player == null) return "";
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Leaderboard leaderboard = LeaderboardManager.getLeaderboard(pitPlayer.savedLeaderboardRef);
		LeaderboardData data = LeaderboardData.getLeaderboardData(leaderboard);
		if(leaderboard == null) return "";

		for(int i = 1; i <= 10; i++) {
			String testString = "leader" + i;
			if(!identifier.equalsIgnoreCase(testString)) continue;
			LeaderboardPosition position = leaderboard.orderedLeaderboard.get(i - 1);
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(position.uuid);
			LeaderboardData.PlayerData playerData = LeaderboardData.getLeaderboardData(LeaderboardManager.leaderboards.get(0)).getValue(offlinePlayer.getUniqueId());
			String rankColor = Leaderboard.getRankColor(offlinePlayer.getUniqueId());

			if(leaderboard instanceof XPLeaderboard) {
				return getColor(i) + String.valueOf(i) + ". " + rankColor + offlinePlayer.getName() + "&7 - " + PrestigeValues.getLeaderboardPrefix(playerData.prestige, playerData.level);
			} else {
				return getColor(i) + String.valueOf(i) + ". " + rankColor + offlinePlayer.getName() + "&7 - " + leaderboard.getDisplayValue(position);
			}

		}
		return null;
	}

	public static ChatColor getColor(int i) {
		switch(i) {
			case 3: return ChatColor.GOLD;
			case 2: return ChatColor.WHITE;
			case 1: return ChatColor.YELLOW;
		}
		return ChatColor.GRAY;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "pitsimlb";
	}

	@Override
	public @NotNull String getAuthor() {
		return "KyroKrypt";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}
}
