package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardPlaceholders extends PlaceholderExpansion {

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier){
		if(player == null) return "";

		for(int i = 1; i <= 10; i++) {
			String testString = "leader" + i;
			if(!identifier.equalsIgnoreCase(testString)) continue;
			LeaderboardPosition position = LeaderboardManager.leaderboards.get(0).orderedLeaderboard.get(i);
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(position.uuid);
			FileConfiguration playerData = APlayerData.getPlayerData(position.uuid).playerData;

			int prestige = playerData.getInt("prestige");
			int level = playerData.getInt("level");
			PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(prestige);
			return "&7" + i + ". &6" + offlinePlayer.getName() + "&7 - " + info.getOpenBracket() + "&e" + AUtil.toRoman(prestige) +
					info.bracketColor + "-" + PrestigeValues.getLevelColor(level) + level + info.getCloseBracket();
		}
		return null;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "pitsim";
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
